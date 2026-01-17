package com.juanma.studentanalytics.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BatchJobRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BatchJobRunner.class);

    private final JobLauncher jobLauncher;
    private final Job importStudentsJob;

    public BatchJobRunner(JobLauncher jobLauncher, Job importStudentsJob) {
        this.jobLauncher = jobLauncher;
        this.importStudentsJob = importStudentsJob;
    }

    @Override
    public void run(String... args) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis()) // parametro unico
                .toJobParameters();

        log.info("batch: lanzando importStudentsJob...");
        jobLauncher.run(importStudentsJob, params);
        log.info("batch: importStudentsJob lanzado");
    }
}
