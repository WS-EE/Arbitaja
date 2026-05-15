package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CompetitorCriteriaResultsResponse(
    @JsonProperty("competitor_id")
    @NotNull Integer competitorId,
    @NotNull String name,
    @NotNull Set<CriterionResultResponse> criteria
) {
}
