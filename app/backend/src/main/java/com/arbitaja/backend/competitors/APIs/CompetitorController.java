package com.arbitaja.backend.competitors.APIs;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.RestController;

@EnableMethodSecurity
@RestController
public class CompetitorController {
    private static final Logger log = LogManager.getLogger(CompetitorController.class);

}
