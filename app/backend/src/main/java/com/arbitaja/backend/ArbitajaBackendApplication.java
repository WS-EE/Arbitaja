package com.arbitaja.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.arbitaja.backend", "com.arbitaja.refactored.backend"})
@EntityScan(basePackages = {"com.arbitaja.backend", "com.arbitaja.refactored.backend"})
@EnableJpaRepositories(basePackages = {"com.arbitaja.backend", "com.arbitaja.refactored.backend"})
public class ArbitajaBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArbitajaBackendApplication.class, args);
    }
}
