package com.arbitaja.refactored.backend.competition.adapter.in.web;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitionJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorSchoolJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorPersonalDataJpaRepository;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class CompetitionWebE2EBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected CompetitionJpaRepository competitionJpaRepository;

    @Autowired
    protected CompetitorJpaRepository competitorJpaRepository;

    @Autowired
    protected CompetitorSchoolJpaRepository schoolJpaRepository;

    @Autowired
    protected CompetitorPersonalDataJpaRepository personalDataJpaRepository;

    @Autowired
    protected UserJpaRepository userJpaRepository;

    protected Integer firstOrganizerId() {
        return userJpaRepository.findByUsername("admin").orElseThrow().getId();
    }

    protected Integer firstSchoolId() {
        return schoolJpaRepository.findAll().stream().findFirst().orElseThrow().getId();
    }

    protected Integer firstCompetitorId() {
        return competitorJpaRepository.findAll().stream().findFirst().orElseThrow().getId();
    }

    protected Integer firstCompetitionId() {
        return competitionJpaRepository.findAll().stream().findFirst().orElseThrow().getId();
    }

    protected String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    protected Timestamp futureTimestamp(long hoursFromNow) {
        return new Timestamp(System.currentTimeMillis() + (hoursFromNow * 3600 * 1000));
    }

    protected Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}

