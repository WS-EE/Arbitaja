package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public record ScoringDashboardResponse(
    @JsonProperty("competition_id")
    Integer competitionId,
    @JsonProperty("competition_name")
    String competitionName,
    Set<CompetitorDashboardResponse> competitors
) {

    public record CompetitorDashboardResponse(
        @JsonProperty("competitor_id")
        Integer competitorId,
        String name,
        @JsonProperty("total_score")
        Double totalScore,
        List<DashboardResultPointResponse> results
    ) {
    }

    public record DashboardResultPointResponse(
        Timestamp timestamp,
        @JsonProperty("point_amount")
        Double pointAmount
    ) {
    }
}
