package com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AddScoringHistoryRequest(
    @NotNull
    @JsonProperty("competition_id")
    Integer competitionId,
    @NotNull
    @JsonProperty("competitor_id")
    Integer competitorId,
    @NotNull
    @JsonProperty("criteria_id")
    Integer criteriaId,
    @NotNull
    @PositiveOrZero
    Double points
) {
}
