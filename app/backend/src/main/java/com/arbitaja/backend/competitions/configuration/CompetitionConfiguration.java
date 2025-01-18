package com.arbitaja.backend.competitions.configuration;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitions.repositories.CompetitionRepository;

import com.arbitaja.backend.users.dataobjects.User;
import com.arbitaja.backend.users.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Time;
import java.time.LocalTime;

@Configuration
public class CompetitionConfiguration {

    private static final Logger log = LogManager.getLogger(CompetitionConfiguration.class);


    @Bean
    CommandLineRunner competitionRunner(
            CompetitionRepository competitionRepository,
            UserRepository userRepository
    ) {

        return args -> {
            if (competitionRepository.count() == 0) {
                User organizer = userRepository.findUserByUsername("Arbitaja");
                competitionRepository.save(new Competition("Noor meister", null, Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now()), organizer));
            }

        };
    }
}
