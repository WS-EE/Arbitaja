package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringHistoryJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringDashboardRowProjection;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringHistoryWithCriterionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public interface ScoringHistoryJpaRepository extends JpaRepository<ScoringHistoryJpaEntity, Integer> {

    /**
     * Single-query running-total dashboard chart.
     *
     * <p>Each scoring event replaces the previous score for the same
     * {@code (competitor, criterion)} pair, so its contribution to the running total
     * is {@code current_points - previous_points_for_same_criterion}. Summing those
     * deltas across all criteria for a competitor in chronological order yields
     * exactly the legacy "sum of latest score per criterion at each timestamp" series,
     * computed entirely in Postgres.</p>
     *
     * <p>The CTE first materialises per-event deltas with {@code LAG}; the outer query
     * then sums them with a second window. Postgres rejects nested window calls in a
     * single SELECT, so the two stages are split.</p>
     *
     * <p>Native query because this {@code LAG}-delta-then-sum pattern is awkward to
     * express in portable JPQL.</p>
     */
    @Query(name = "ScoringHistoryJpaEntity.findRunningTotalsForCompetition", nativeQuery = true)
    List<ScoringDashboardRowProjection> findRunningTotalsForCompetition(
        @Param("competitionId") Integer competitionId,
        @Param("cutoff") Timestamp cutoff
    );

    /**
     * Returns the most recent scoring history row for every (competitor, criterion) pair
     * within a competition in a single query. Uses {@code DISTINCT ON} to pick the latest
     * row per pair in index order, which avoids the per-row correlated subquery cost of the
     * previous approach.
     */
    @Query(name = "ScoringHistoryJpaEntity.findLatestPerCompetitorAndCriterion", nativeQuery = true)
    List<ScoringHistoryWithCriterionProjection> findLatestPerCompetitorAndCriterion(
        @Param("competitionId") Integer competitionId
    );

    /**
     * Same as {@link #findLatestPerCompetitorAndCriterion} but scoped to a single competitor.
     */
    @Query(name = "ScoringHistoryJpaEntity.findLatestPerCriterionForCompetitor", nativeQuery = true)
    List<ScoringHistoryWithCriterionProjection> findLatestPerCriterionForCompetitor(
        @Param("competitionId") Integer competitionId,
        @Param("competitorId") Integer competitorId
    );

    /**
     * Bulk-deletes all scoring history rows for a competition. Used by E2E tests to
     * normalise dashboard fixtures that depend on running totals — production code
     * should not call this.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ScoringHistoryJpaEntity sh WHERE sh.competitionId = :competitionId")
    void deleteAllByCompetitionId(@Param("competitionId") Integer competitionId);
}
