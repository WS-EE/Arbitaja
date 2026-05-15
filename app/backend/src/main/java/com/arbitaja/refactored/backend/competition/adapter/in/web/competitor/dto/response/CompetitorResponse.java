package com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CompetitorResponse(
    @NotNull
    Integer id,
    String alias,
    @JsonProperty("public_display_name_type")
    Integer publicDisplayNameType,
    @JsonProperty("personal_data")
    PersonalDataResponse personalData
) {
    public record PersonalDataResponse(
        @NotNull
        Integer id,
        @JsonProperty("full_name")
        String fullName,
        String email,
        School school
    ) {
        public record School(
            @NotNull
            Integer id,
            String name
        ){}
    }
}

