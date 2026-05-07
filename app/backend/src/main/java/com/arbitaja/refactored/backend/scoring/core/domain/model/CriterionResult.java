package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Latest score for a single criterion.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "criterionId")
public class CriterionResult {

    private Integer criterionId;
    private String criterionName;
    private Double points;
}
