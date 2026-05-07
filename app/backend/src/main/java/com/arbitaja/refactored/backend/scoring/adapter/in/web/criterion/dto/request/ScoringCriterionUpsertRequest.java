package com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ScoringCriterionUpsertRequest(
    @NotBlank
    String name,
    String description,
    @JsonProperty("is_manual")
    Boolean manual,
    @NotNull
    @PositiveOrZero
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
    Integer criteriaTemplateId,
    @JsonProperty("competition_id")
    Integer competitionId
) {
}
