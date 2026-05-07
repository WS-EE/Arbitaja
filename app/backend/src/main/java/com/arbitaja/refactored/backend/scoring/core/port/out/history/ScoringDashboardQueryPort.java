package com.arbitaja.refactored.backend.scoring.core.port.out.history;

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
     * Loads all scoring history rows for a competition with {@code created_at &le; cutoff},
     * ordered by competitor and creation timestamp so callers can compute running totals
     * with a single linear pass.
     */
    List<ScoringHistoryEntry> findHistoryForCompetition(@NonNull Integer competitionId, @NonNull Timestamp cutoff);

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
