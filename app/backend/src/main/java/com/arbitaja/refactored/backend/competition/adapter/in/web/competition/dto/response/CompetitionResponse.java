package com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.Set;

public record CompetitionResponse(
    @NotNull
    Integer id,
    @NotNull
    String name,
    @JsonProperty("start_time")
    Timestamp startTime,
    @JsonProperty("end_time")
    Timestamp endTime,
    @JsonProperty("score_showtime")
    Timestamp scoreShowtime,
    @JsonProperty("publish_scores")
    Boolean publishScores,
    OrganizerResponse organizer,
    Set<CompetitorResponse> competitors
) {
    public record OrganizerResponse(
        @NotNull
        Integer id,
        @JsonProperty("full_name")
        String fullName,
        String username
    ) {
    }

    public record CompetitorResponse(
        @NotNull
        Integer id,
        @JsonProperty("full_name")
        String fullName,
        String alias
    ) {
    }
}

