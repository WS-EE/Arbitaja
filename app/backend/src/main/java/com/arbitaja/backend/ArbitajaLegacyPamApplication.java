package com.arbitaja.backend;

import org.springframework.boot.SpringApplication;

public class ArbitajaLegacyPamApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ArbitajaBackendApplication.class);
        application.setAdditionalProfiles("pam-legacy");
        application.run(args);
    }
}

