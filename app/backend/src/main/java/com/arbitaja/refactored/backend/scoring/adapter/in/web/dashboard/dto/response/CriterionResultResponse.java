package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CriterionResultResponse(
    @JsonProperty("criterion_id")
    @NotNull Integer criterionId,
    @JsonProperty("criterion_name")
    @NotNull String criterionName,
    @NotNull Double points
) {
}
