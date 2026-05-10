package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection;

import java.sql.Timestamp;

/**
 * Spring Data projection for the running-total dashboard query.
 *
 * <p>{@code runningTotal} is computed by the database window function,
 * so the application layer does not need to track per-criterion state.</p>
 */
public interface ScoringDashboardRowProjection {

    Integer getCompetitorId();

    Timestamp getTimestamp();

    Double getRunningTotal();
}
