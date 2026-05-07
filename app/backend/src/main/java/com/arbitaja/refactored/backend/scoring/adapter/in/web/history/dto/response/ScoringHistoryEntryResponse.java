package com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public record ScoringHistoryEntryResponse(
    Integer id,
    @JsonProperty("competition_id")
    Integer competitionId,
    @JsonProperty("competitor_id")
    Integer competitorId,
    @JsonProperty("criteria_id")
    Integer criteriaId,
    @JsonProperty("criteria_name")
    String criteriaName,
    @JsonProperty("points_given")
    Double pointsGiven,
    @JsonProperty("created_at")
    Timestamp createdAt
) {
}
