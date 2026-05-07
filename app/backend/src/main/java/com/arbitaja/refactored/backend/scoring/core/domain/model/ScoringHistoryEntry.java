package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * Single recorded score for a competitor against a criterion.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ScoringHistoryEntry {

    private Integer id;
    private Integer competitionId;
    private Integer competitorId;
    private Integer scoringCriterionId;
    private String scoringCriterionName;
    private Double pointsGiven;
    private Timestamp createdAt;
}
