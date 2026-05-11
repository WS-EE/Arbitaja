package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CriterionResultResponse(
    @JsonProperty("criterion_id")
    Integer criterionId,
    @JsonProperty("criterion_name")
    String criterionName,
    Double points
) {
}
