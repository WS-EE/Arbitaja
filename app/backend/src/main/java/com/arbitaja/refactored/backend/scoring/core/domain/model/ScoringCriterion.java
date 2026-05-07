package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Core domain model for a scoring criterion definition.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ScoringCriterion {

    private Integer id;
    private String name;
    private String description;
    private Boolean manual;
    private Double totalPoints;
    private Boolean generalized;
    private String expectedResult;
    private Boolean template;
    private Integer visibilityLevel;
    private Integer scoringHostId;
    private Integer criteriaTemplateId;
}
