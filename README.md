# Student Analytics  
**Feedback 2 – Programación Concurrente**

Proyecto desarrollado con **Spring Boot** que integra en una única aplicación los siguientes conceptos del temario:

- **Spring Batch** para procesamiento por lotes
- **API reactiva con Spring WebFlux**
- Simulación de arquitectura de microservicios:
  - API Gateway
  - Circuit Breaker (Resilience4j)
  - Trazabilidad mediante `traceId`
- **Base de datos H2 en memoria**

> El objetivo del proyecto es demostrar el uso combinado de concurrencia, reactividad y tolerancia a fallos, tal y como se solicita en el enunciado del Feedback 2.

---

## 📦 Tecnologías utilizadas

- Java 17  
- Spring Boot 3.3.x  
- Spring WebFlux  
- Spring Batch (v5)  
- Spring Data JPA  
- H2 Database (in-memory)  
- Resilience4j (Circuit Breaker)  
- Maven  

---

## 📂 Estructura del proyecto
```
STUDENTANALYTICS\SRC
├───main
│   ├───java
│   │   └───com
│   │       └───juanma
│   │           └───studentanalytics
│   │               │   StudentanalyticsApplication.java
│   │               │   
│   │               ├───batch
│   │               │       BatchConfig.java
│   │               │       BatchJobRunner.java
│   │               │       
│   │               ├───gateway
│   │               │       GatewayController.java
│   │               │       StudentClientService.java
│   │               │       
│   │               ├───student
│   │               │       Student.java
│   │               │       StudentController.java
│   │               │       StudentRepository.java
│   │               │       
│   │               └───tracing
│   └───resources
│           application.properties
│           students.csv
│           
└───test
    └───java
        └───com
            └───juanma
                └───studentanalytics
                        StudentanalyticsApplicationTests.java

```                        

---

## ▶️ Cómo ejecutar la aplicación

### Requisitos
- Java 17
- Maven

### Arranque
Desde la raíz del proyecto:

```
mvn spring-boot:run

```
La aplicación se levanta en:
http://localhost:8080

## 🧮 Proceso Batch (Spring Batch)

Al arrancar la aplicación se ejecuta automáticamente un Job de Spring Batch que:

Lee el fichero students.csv

Procesa los datos mediante el patrón:

ItemReader → ItemProcessor → ItemWriter

Inserta los datos en una tabla students en una base de datos H2 en memoria

Usa un chunk size de 5

## 📍 Fichero de entrada:
```
src/main/resources/students.csv
```
Formato:
```
id,name,averageGrade
1,Ana,7.5
2,Luis,5.2
3,Marta,8.9
```
## 🗄️ Base de datos H2

Base de datos en memoria

Tablas creadas automáticamente al arrancar

Consola H2 disponible en: http://localhost:8080/h2-console

Datos de conexión:

JDBC URL: 
```
jdbc:h2:mem:studentdb
```
Usuario: 
```
sa
```
Contraseña: (vacía)

##  🔁 API reactiva (Spring WebFlux)
GET /students

Devuelve todos los estudiantes almacenados en la base de datos.
http://localhost:8080/students

GET /students/top?min=7.0

Devuelve los estudiantes cuya nota media es mayor o igual al valor indicado.
http://localhost:8080/students/top?min=7.0

Aunque el acceso a datos se realiza mediante JPA (bloqueante), los endpoints exponen una API reactiva (Flux), delegando la ejecución a hilos elásticos para no bloquear el hilo reactivo.

## 🌐 API Gateway + Circuit Breaker + traceId
GET /api/public/students

Este endpoint actúa como API Gateway y realiza las siguientes acciones:

Genera un traceId único por petición

Registra el traceId en los logs

Llama a un servicio interno mediante WebClient

Aplica un Circuit Breaker (Resilience4j)

Devuelve el resultado al cliente

Incluye el traceId en el header de la respuesta

```
curl -i http://localhost:8080/api/public/students
```
Header de ejemplo:
```
X-Trace-Id: 550e8400-e29b-41d4-a716-446655440000
```
## 🔥 Prueba del Circuit Breaker (modo demo)

En application.properties se define la URL del servicio interno:
```
student.service.base-url=http://localhost:8080
```
Para forzar el fallo y comprobar el fallback, basta con:

Comentar la URL correcta

Descomentar la URL incorrecta
```
# student.service.base-url=http://localhost:8080
student.service.base-url=http://localhost:9999
```
Tras reiniciar la aplicación:
```
curl http://localhost:8080/api/public/students
```
➡️ El Circuit Breaker se activa y se ejecuta el fallback, devolviendo una lista vacía.

## ✅ Requisitos del enunciado cubiertos

Spring Batch con CSV → H2 (auto-run, chunk, reader/processor/writer)

API reactiva con WebFlux

API Gateway simulado

Circuit Breaker con Resilience4j

Trazabilidad con traceId

Base de datos en memoria

Pruebas manuales documentadas
