package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CompetitionScoringCriteriaResultsResponse(
    @JsonProperty("competition_id")
    @NotNull Integer competitionId,
    @JsonProperty("competition_name")
    @NotNull String competitionName,
    @NotNull Set<CompetitorCriteriaResultsResponse> competitors
) {
}
