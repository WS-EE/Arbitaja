package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record CompetitionScoringCriteriaResultsResponse(
    @JsonProperty("competition_id")
    Integer competitionId,
    @JsonProperty("competition_name")
    String competitionName,
    Set<CompetitorCriteriaResultsResponse> competitors
) {
}
