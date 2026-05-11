package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record CompetitorCriteriaResultsResponse(
    @JsonProperty("competitor_id")
    Integer competitorId,
    String name,
    Set<CriterionResultResponse> criteria
) {
}
