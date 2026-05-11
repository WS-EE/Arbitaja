package com.arbitaja.refactored.backend.scoring.adapter.in.web;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitionJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorJpaRepository;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.request.ScoringCriterionUpsertRequest;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository.ScoringCriterionJpaRepository;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository.ScoringHistoryJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ScoringWebE2EBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected CompetitionJpaRepository competitionJpaRepository;

    @Autowired
    protected CompetitorJpaRepository competitorJpaRepository;

    @Autowired
    protected ScoringCriterionJpaRepository scoringCriterionJpaRepository;

    @Autowired
    protected ScoringHistoryJpaRepository scoringHistoryJpaRepository;

    protected Integer firstCompetitionId() {
        return competitionJpaRepository.findAll().stream().findFirst().orElseThrow().getId();
    }

    protected Integer firstCompetitorId() {
        return competitorJpaRepository.findAll().stream().findFirst().orElseThrow().getId();
    }

    protected String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * Marks the first competition active (start in the past, end in the future) so scoring
     * tests can record results without depending on whether the seed timestamps have aged out
     * since the database was first migrated.
     */
    protected void ensureFirstCompetitionIsActive() {
        CompetitionJpaEntity competition = competitionJpaRepository.findById(firstCompetitionId()).orElseThrow();
        Instant now = Instant.now();
        competition.setStartTime(Timestamp.from(now.minusSeconds(3600)));
        competition.setEndTime(Timestamp.from(now.plusSeconds(86400)));
        competition.setScoreShowtime(Timestamp.from(now.plusSeconds(43200)));
        competitionJpaRepository.save(competition);
    }

    protected Integer createCriterionLinkedToCompetition(double totalPoints) throws Exception {
        ScoringCriterionUpsertRequest request = new ScoringCriterionUpsertRequest(
            unique("crit"), "desc", true, totalPoints, false, "expected", false, 0, null, null, firstCompetitionId()
        );

        MvcResult result = mockMvc.perform(post("/v2/scoring/criteria")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asInt();
    }
}
