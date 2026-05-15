package com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public record ScoringHistoryEntryResponse(
    @NotNull Integer id,
    @JsonProperty("competition_id")
    @NotNull Integer competitionId,
    @JsonProperty("competitor_id")
    @NotNull Integer competitorId,
    @JsonProperty("criteria_id")
    @NotNull Integer criteriaId,
    @JsonProperty("criteria_name")
    @NotNull String criteriaName,
    @JsonProperty("points_given")
    @NotNull Double pointsGiven,
    @JsonProperty("created_at")
    @NotNull Timestamp createdAt
) {
}
