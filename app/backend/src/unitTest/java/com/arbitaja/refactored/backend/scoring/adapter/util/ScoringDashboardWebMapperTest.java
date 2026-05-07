package com.arbitaja.refactored.backend.scoring.adapter.util;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CompetitionScoringCriteriaResultsResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CompetitorCriteriaResultsResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.ScoringDashboardResponse;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitionScoringCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorDashboard;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CriterionResult;
import com.arbitaja.refactored.backend.scoring.core.domain.model.DashboardResultPoint;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringDashboard;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoringDashboardWebMapperTest {

    private final ScoringDashboardWebMapper mapper = new ScoringDashboardWebMapper();

    @Test
    void scoringDashboardMapsCompetitorsAndResults() {
        Timestamp t = Timestamp.valueOf("2026-04-06 12:00:00");
        CompetitorDashboard alice = CompetitorDashboard.builder()
            .competitorId(10).competitorName("alice").totalScore(7.0)
            .results(List.of(DashboardResultPoint.builder().timestamp(t).pointAmount(7.0).build()))
            .build();
        Set<CompetitorDashboard> competitors = new LinkedHashSet<>();
        competitors.add(alice);
        ScoringDashboard dashboard = ScoringDashboard.builder()
            .competitionId(1).competitionName("Final").competitors(competitors)
            .build();

        ScoringDashboardResponse response = mapper.toResponse(dashboard);

        assertEquals(1, response.competitionId());
        assertEquals("Final", response.competitionName());
        assertEquals(1, response.competitors().size());
        ScoringDashboardResponse.CompetitorDashboardResponse competitorResponse =
            response.competitors().iterator().next();
        assertEquals("alice", competitorResponse.name());
        assertEquals(7.0, competitorResponse.totalScore());
        assertEquals(t, competitorResponse.results().getFirst().timestamp());
    }

    @Test
    void competitionScoringCriteriaResultsResponseMapsAllNestedFields() {
        CriterionResult criterionResult = CriterionResult.builder()
            .criterionId(100).criterionName("ssh").points(4.0).build();
        Set<CriterionResult> criteria = new LinkedHashSet<>();
        criteria.add(criterionResult);
        CompetitorCriteriaResults aliceResults = CompetitorCriteriaResults.builder()
            .competitorId(10).competitorName("alice").criteria(criteria)
            .build();
        Set<CompetitorCriteriaResults> competitors = new LinkedHashSet<>();
        competitors.add(aliceResults);
        CompetitionScoringCriteriaResults results = CompetitionScoringCriteriaResults.builder()
            .competitionId(1).competitionName("Final").competitors(competitors)
            .build();

        CompetitionScoringCriteriaResultsResponse response = mapper.toResponse(results);

        assertEquals(1, response.competitionId());
        CompetitorCriteriaResultsResponse competitorResponse = response.competitors().iterator().next();
        assertEquals("alice", competitorResponse.name());
        assertEquals(4.0, competitorResponse.criteria().iterator().next().points());
    }

    @Test
    void competitorCriteriaResultsResponseHandlesEmptyCriteria() {
        CompetitorCriteriaResults aliceResults = CompetitorCriteriaResults.builder()
            .competitorId(10).competitorName("alice").criteria(new LinkedHashSet<>())
            .build();

        CompetitorCriteriaResultsResponse response = mapper.toResponse(aliceResults);

        assertEquals(10, response.competitorId());
        assertEquals(0, response.criteria().size());
    }
}
