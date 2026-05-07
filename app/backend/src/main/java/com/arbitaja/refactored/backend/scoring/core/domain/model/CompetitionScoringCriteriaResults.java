package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Per-competition view of the latest scoring criterion result for every competitor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionScoringCriteriaResults {

    private Integer competitionId;
    private String competitionName;

    @Builder.Default
    private Set<CompetitorCriteriaResults> competitors = new LinkedHashSet<>();
}
