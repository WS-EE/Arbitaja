package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Aggregate view of a competition's scoring dashboard.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringDashboard {

    private Integer competitionId;
    private String competitionName;

    @Builder.Default
    private Set<CompetitorDashboard> competitors = new LinkedHashSet<>();
}
