package com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ScoringCriterionResponse(
    Integer id,
    String name,
    String description,
    @JsonProperty("is_manual")
    Boolean manual,
    @JsonProperty("total_points")
    Double totalPoints,
    @JsonProperty("is_generalized")
    Boolean generalized,
    @JsonProperty("expected_result")
    String expectedResult,
    @JsonProperty("is_template")
    Boolean template,
    @JsonProperty("visibility_level")
    Integer visibilityLevel,
    @JsonProperty("scoring_host_id")
    Integer scoringHostId,
    @JsonProperty("criteria_template_id")
    Integer criteriaTemplateId
) {
}
