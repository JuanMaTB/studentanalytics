package com.juanma.studentanalytics.gateway;

import com.juanma.studentanalytics.student.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
public class GatewayController {

    private static final Logger log = LoggerFactory.getLogger(GatewayController.class);

    private final StudentClientService studentClientService;

    public GatewayController(StudentClientService studentClientService) {
        this.studentClientService = studentClientService;
    }

    @GetMapping("/api/public/students")
    public Mono<ResponseEntity<List<Student>>> publicStudents() {
        String traceId = UUID.randomUUID().toString();

        log.info("gateway: GET /api/public/students traceId={}", traceId);

        return studentClientService.getStudents(traceId)  // <-- aqui esta el cambio
                .map(list -> ResponseEntity.ok()
                        .header("X-Trace-Id", traceId)
                        .body(list));
    }
}
