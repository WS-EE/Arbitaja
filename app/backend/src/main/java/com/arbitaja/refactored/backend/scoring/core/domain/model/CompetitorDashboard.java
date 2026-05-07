package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-competitor entry on the scoring dashboard, including the running total over time.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitorDashboard {

    private Integer competitorId;
    private String competitorName;
    private Double totalScore;

    @Builder.Default
    private List<DashboardResultPoint> results = new ArrayList<>();
}
