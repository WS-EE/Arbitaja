package com.arbitaja.backend.users.configurations;


import com.arbitaja.backend.users.repositories.PermissionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionConfig {
    private static final Logger log = LogManager.getLogger(PermissionConfig.class);

    @Bean
    CommandLineRunner commandLineRunner(PermissionRepository permissionRepository){

        return args -> {
            log.info("Permissions in the database on startup: " + permissionRepository.findAll());
        };
    }
}
