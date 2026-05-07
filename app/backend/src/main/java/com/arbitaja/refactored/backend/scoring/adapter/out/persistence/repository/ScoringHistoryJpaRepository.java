package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringHistoryJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringHistoryWithCriterionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface ScoringHistoryJpaRepository extends JpaRepository<ScoringHistoryJpaEntity, Integer> {

    /**
     * Loads every scoring history row for a competition with {@code created_at &le; cutoff}
     * joined with the criterion name. Replaces the legacy per-competitor query loop with a
     * single round trip; callers compute running totals client-side from the ordered rows.
     */
    @Query("""
        SELECT sh.id AS id,
               sh.competitionId AS competitionId,
               sh.competitorId AS competitorId,
               sh.scoringCriteriaId AS scoringCriterionId,
               sc.name AS scoringCriterionName,
               sh.pointsGiven AS pointsGiven,
               sh.createdAt AS createdAt
        FROM ScoringHistoryJpaEntity sh
        LEFT JOIN ScoringCriterionJpaEntity sc ON sc.id = sh.scoringCriteriaId
        WHERE sh.competitionId = :competitionId
          AND sh.createdAt <= :cutoff
        ORDER BY sh.competitorId ASC, sh.createdAt ASC
    """)
    List<ScoringHistoryWithCriterionProjection> findHistoryForCompetition(
        @Param("competitionId") Integer competitionId,
        @Param("cutoff") Timestamp cutoff
    );

    /**
     * Returns the most recent scoring history row for every (competitor, criterion) pair
     * within a competition in a single query. Uses a correlated subquery to pick the row
     * whose {@code created_at} is the maximum for that pair, which lets the application
     * service render the criteria-result view without per-criterion follow-up queries.
     */
    @Query("""
        SELECT sh.id AS id,
               sh.competitionId AS competitionId,
               sh.competitorId AS competitorId,
               sh.scoringCriteriaId AS scoringCriterionId,
               sc.name AS scoringCriterionName,
               sh.pointsGiven AS pointsGiven,
               sh.createdAt AS createdAt
        FROM ScoringHistoryJpaEntity sh
        LEFT JOIN ScoringCriterionJpaEntity sc ON sc.id = sh.scoringCriteriaId
        WHERE sh.competitionId = :competitionId
          AND sh.createdAt = (
              SELECT MAX(sh2.createdAt) FROM ScoringHistoryJpaEntity sh2
              WHERE sh2.competitionId = sh.competitionId
                AND sh2.competitorId = sh.competitorId
                AND sh2.scoringCriteriaId = sh.scoringCriteriaId
          )
    """)
    List<ScoringHistoryWithCriterionProjection> findLatestPerCompetitorAndCriterion(
        @Param("competitionId") Integer competitionId
    );

    /**
     * Same as {@link #findLatestPerCompetitorAndCriterion} but scoped to a single competitor.
     */
    @Query("""
        SELECT sh.id AS id,
               sh.competitionId AS competitionId,
               sh.competitorId AS competitorId,
               sh.scoringCriteriaId AS scoringCriterionId,
               sc.name AS scoringCriterionName,
               sh.pointsGiven AS pointsGiven,
               sh.createdAt AS createdAt
        FROM ScoringHistoryJpaEntity sh
        LEFT JOIN ScoringCriterionJpaEntity sc ON sc.id = sh.scoringCriteriaId
        WHERE sh.competitionId = :competitionId
          AND sh.competitorId = :competitorId
          AND sh.createdAt = (
              SELECT MAX(sh2.createdAt) FROM ScoringHistoryJpaEntity sh2
              WHERE sh2.competitionId = sh.competitionId
                AND sh2.competitorId = sh.competitorId
                AND sh2.scoringCriteriaId = sh.scoringCriteriaId
          )
    """)
    List<ScoringHistoryWithCriterionProjection> findLatestPerCriterionForCompetitor(
        @Param("competitionId") Integer competitionId,
        @Param("competitorId") Integer competitorId
    );
}
