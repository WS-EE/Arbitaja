package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection;

import java.sql.Timestamp;

/**
 * Spring Data projection that returns the scoring history row joined with the
 * criterion name in a single query. Lets dashboard endpoints avoid follow-up
 * lookups for the criterion entity.
 */
public interface ScoringHistoryWithCriterionProjection {

    Integer getId();

    Integer getCompetitionId();

    Integer getCompetitorId();

    Integer getScoringCriterionId();

    String getScoringCriterionName();

    Double getPointsGiven();

    Timestamp getCreatedAt();
}
