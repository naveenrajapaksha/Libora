package com.libora.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LiboraBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                LiboraBackendApplication.class,
                args
        );
    }
}