package com.juanma.studentanalytics.gateway;

import com.juanma.studentanalytics.student.Student;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class StudentClientService {

    private static final Logger log = LoggerFactory.getLogger(StudentClientService.class);

    private final WebClient webClient;

    public StudentClientService(WebClient.Builder builder,
                                @Value("${student.service.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "studentServiceCB", fallbackMethod = "fallbackStudents")
    public Mono<List<Student>> getStudents(String traceId) {
        return webClient.get()
                .uri("/students")
                .header("X-Trace-Id", traceId) // propagamos traceId al "microservicio" interno
                .retrieve()
                .bodyToFlux(Student.class)
                .collectList()
                .timeout(Duration.ofSeconds(2)); // timeout corto para mostrar el patron
    }

    // fallbackMethod: mismos parametros que el metodo principal + Throwable al final
    private Mono<List<Student>> fallbackStudents(String traceId, Throwable ex) {
        log.warn("fallbackStudents activado. traceId={}, causa={}", traceId, ex.toString());
        return Mono.just(Collections.emptyList());
    }
}
