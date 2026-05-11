package com.arbitaja.refactored.backend.scoring.core.application;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configures supporting beans the scoring application services need.
 */
@Configuration
public class ScoringApplicationConfig {

    @Bean
    @ConditionalOnMissingBean
    public Clock scoringClock() {
        return Clock.systemDefaultZone();
    }
}
