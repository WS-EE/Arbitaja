package com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompetitorResponse(
    Integer id,
    String alias,
    @JsonProperty("public_display_name_type")
    Integer publicDisplayNameType,
    PersonalDataResponse personalData
) {
    public record PersonalDataResponse(
        Integer id,
        @JsonProperty("full_name")
        String fullName,
        String email,
        @JsonProperty("school_id")
        Integer schoolId
    ) {
    }
}

