package com.juanma.studentanalytics.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@RestController
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // 1) GET /students -> Flux<Student>
    @GetMapping("/students")
    public Flux<Student> getAllStudents() {
        return Flux.defer(() -> Flux.fromIterable(studentRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // 2) GET /students/top?min=7.0 -> filtrado reactivo
    @GetMapping("/students/top")
    public Flux<Student> getTopStudents(@RequestParam("min") double min) {
        return Flux.defer(() -> Flux.fromIterable(studentRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .filter(s -> s.getAverageGrade() != null && s.getAverageGrade() >= min);
    }

    // 3) GET /students/stream (opcional) -> simula streaming
    @GetMapping("/students/stream")
    public Flux<Student> streamStudents() {
        return Flux.defer(() -> Flux.fromIterable(studentRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .delayElements(Duration.ofSeconds(1));
    }
}
