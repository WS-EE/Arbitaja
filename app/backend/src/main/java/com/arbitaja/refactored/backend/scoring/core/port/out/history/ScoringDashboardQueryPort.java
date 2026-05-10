package com.arbitaja.refactored.backend.scoring.core.port.out.history;

import com.arbitaja.refactored.backend.scoring.core.domain.model.DashboardResultRow;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import lombok.NonNull;

import java.sql.Timestamp;
import java.util.List;

/**
 * Output port for optimised dashboard queries.
 *
 * <p>Implementations are expected to fetch all required scoring history rows in a
 * single round-trip rather than issuing per-competitor or per-criterion follow-up
 * queries (which is the legacy pattern).</p>
 */
public interface ScoringDashboardQueryPort {

    /**
     * Returns the running-total chart for a competition. Each row already carries
     * {@code runningTotal} at that timestamp for that competitor, computed in the
     * database with a window function — the application service does not need to
     * accumulate per-criterion state in memory.
     *
     * <p>Rows are ordered by competitor and creation timestamp so callers can
     * group them in a single linear pass.</p>
     */
    List<DashboardResultRow> findHistoryForCompetition(@NonNull Integer competitionId, @NonNull Timestamp cutoff);

    /**
     * Loads, in a single query, the most recent score for every (competitor, criterion)
     * pair within a competition.
     */
    List<ScoringHistoryEntry> findLatestPerCompetitorAndCriterion(@NonNull Integer competitionId);

    /**
     * Loads, in a single query, the most recent score for a single competitor for every
     * criterion within a competition.
     */
    List<ScoringHistoryEntry> findLatestPerCriterionForCompetitor(@NonNull Integer competitionId, @NonNull Integer competitorId);
}
