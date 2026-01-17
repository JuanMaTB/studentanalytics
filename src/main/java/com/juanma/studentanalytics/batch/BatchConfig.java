package com.juanma.studentanalytics.batch;

import com.juanma.studentanalytics.student.Student;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Student> studentReader() {
        return new FlatFileItemReaderBuilder<Student>()
                .name("studentReader")
                .resource(new ClassPathResource("students.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("id", "name", "averageGrade")
                .targetType(Student.class)
                .build();
    }

    @Bean
    public ItemProcessor<Student, Student> studentProcessor() {
        return student -> {
            if (student.getName() != null) {
                student.setName(student.getName().trim().toUpperCase());
            }
            return student;
        };
    }

    @Bean
    public JpaItemWriter<Student> studentWriter(EntityManagerFactory emf) {
        JpaItemWriter<Student> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emf);
        return writer;
    }

    @Bean
    public Step importStudentsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Student> studentReader,
            ItemProcessor<Student, Student> studentProcessor,
            JpaItemWriter<Student> studentWriter
    ) {
        return new StepBuilder("importStudentsStep", jobRepository)
                .<Student, Student>chunk(5, transactionManager)
                .reader(studentReader)
                .processor(studentProcessor)
                .writer(studentWriter)
                .build();
    }

    @Bean
    public Job importStudentsJob(JobRepository jobRepository, Step importStudentsStep) {
        return new JobBuilder("importStudentsJob", jobRepository)
                .start(importStudentsStep)
                .build();
    }
}
