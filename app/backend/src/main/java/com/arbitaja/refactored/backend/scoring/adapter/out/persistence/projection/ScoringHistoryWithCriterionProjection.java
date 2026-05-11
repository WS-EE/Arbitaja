package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection;

import java.sql.Timestamp;

public record ScoringHistoryWithCriterionProjection(
    Integer id,
    Integer competitionId,
    Integer competitorId,
    Integer scoringCriterionId,
    String scoringCriterionName,
    Double pointsGiven,
    Timestamp createdAt
) {}
