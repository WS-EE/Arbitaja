package com.arbitaja.refactored.backend.scoring.core.port.in.dashboard;

import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitionScoringCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringDashboard;
import lombok.NonNull;

/**
 * Input port for fetching scoring dashboard projections.
 */
public interface GetScoringDashboardUseCase {

    /**
     * Returns the running-total dashboard for a competition.
     *
     * @param competitionId competition id
     * @param viewerIsAdmin {@code true} when the viewer should see the entire history,
     *                      {@code false} when the viewer should only see entries up to
     *                      the competition's score showtime
     */
    ScoringDashboard getDashboard(@NonNull Integer competitionId, boolean viewerIsAdmin);

    CompetitionScoringCriteriaResults getCriteriaResultsForCompetition(@NonNull Integer competitionId, boolean viewerIsAdmin);

    CompetitorCriteriaResults getCriteriaResultsForCompetitor(@NonNull Integer competitionId, @NonNull Integer competitorId, boolean viewerIsAdmin);
}
