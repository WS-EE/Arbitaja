package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection;

import java.sql.Timestamp;

/**
 * Slim projection of the {@code competition} table needed for scoring use cases.
 */
public interface ScoringCompetitionProjection {

    Integer getId();

    String getName();

    Timestamp getStartTime();

    Timestamp getEndTime();

    Timestamp getScoreShowtime();

    Boolean getPublishScores();
}
