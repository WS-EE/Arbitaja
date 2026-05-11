package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Per-competitor latest result for each criterion.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "competitorId")
public class CompetitorCriteriaResults {

    private Integer competitorId;
    private String competitorName;

    @Builder.Default
    private Set<CriterionResult> criteria = new LinkedHashSet<>();
}
