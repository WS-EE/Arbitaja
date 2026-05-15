package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public record ScoringDashboardResponse(
    @NotNull Set<CompetitorDashboardResponse> competitors
) {

    public record CompetitorDashboardResponse(
        @NotNull String name,
        @JsonProperty("total_score")
        @NotNull Double totalScore,
        @NotNull List<DashboardResultPointResponse> results
    ) {
    }

    public record DashboardResultPointResponse(
        @NotNull Timestamp timestamp,
        @JsonProperty("point_amount")
        @NotNull Double pointAmount
    ) {
    }
}
