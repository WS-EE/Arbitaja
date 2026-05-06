package com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public record CompetitionUpsertRequest(
    @NotBlank
    String name,
    @NotNull
    @JsonProperty("start_time")
    Timestamp startTime,
    @NotNull
    @JsonProperty("end_time")
    Timestamp endTime,
    @JsonProperty("score_showtime")
    Timestamp scoreShowtime,
    @JsonProperty("publish_scores")
    Boolean publishScores,
    @NotNull(message = "OrganizerId must be provided")
    @JsonProperty("organizer_id")
    Integer organizerId
) {
}

