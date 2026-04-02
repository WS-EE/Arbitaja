package com.arbitaja.backend;
import org.springframework.boot.SpringApplication;
public class ArbitajaHexPamApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ArbitajaBackendApplication.class);
        application.setAdditionalProfiles("pam-hex");
        application.run(args);
    }
}
