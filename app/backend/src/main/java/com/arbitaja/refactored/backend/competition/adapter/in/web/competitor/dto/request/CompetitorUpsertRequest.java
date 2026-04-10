package com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompetitorUpsertRequest(
    @NotBlank
    String alias,
    @NotNull
    @JsonProperty("public_display_name_type")
    Integer publicDisplayNameType,
    @JsonProperty("personal_data_id")
    Integer personalDataId,
    @JsonProperty("full_name")
    String fullName,
    String email,
    @JsonProperty("school_id")
    Integer schoolId
) {
}

