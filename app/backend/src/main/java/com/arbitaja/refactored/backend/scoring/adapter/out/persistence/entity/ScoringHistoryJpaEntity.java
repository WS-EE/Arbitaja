package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringDashboardRowProjection;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringHistoryWithCriterionProjection;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

/**
 * JPA entity for scoring history rows.
 */
@Entity
@Table(name = "scoring_history")
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "ScoringHistoryJpaEntity.findRunningTotalsForCompetition",
        query = """
            WITH deltas AS (
                SELECT
                  sh.competitor_id,
                  sh.created_at,
                  sh.id,
                  sh.points_given::numeric - COALESCE(LAG(sh.points_given::numeric) OVER (
                      PARTITION BY sh.competitor_id, sh.scoring_criteria_id
                      ORDER BY sh.created_at, sh.id
                  ), 0) AS delta
                FROM scoring_history sh
                WHERE sh.competition_id = :competitionId
                  AND sh.created_at <= :cutoff
                  AND sh.competitor_id IS NOT NULL
            )
            SELECT
              d.competitor_id AS competitorId,
              d.created_at    AS timestamp,
              SUM(d.delta) OVER (
                  PARTITION BY d.competitor_id
                  ORDER BY d.created_at, d.id
              )               AS runningTotal
            FROM deltas d
            ORDER BY d.competitor_id, d.created_at, d.id
            """,
        resultSetMapping = "ScoringDashboardRowMapping"
    ),
    @NamedNativeQuery(
        name = "ScoringHistoryJpaEntity.findLatestPerCompetitorAndCriterion",
        query = """
            SELECT DISTINCT ON (sh.competitor_id, sh.scoring_criteria_id)
                sh.id                  AS id,
                sh.competition_id      AS competitionId,
                sh.competitor_id       AS competitorId,
                sh.scoring_criteria_id AS scoringCriterionId,
                sc.name                AS scoringCriterionName,
                sh.points_given        AS pointsGiven,
                sh.created_at          AS createdAt
            FROM scoring_history sh
            LEFT JOIN scoring_criteria sc ON sc.id = sh.scoring_criteria_id
            WHERE sh.competition_id = :competitionId
              AND sh.competitor_id IS NOT NULL
            ORDER BY sh.competitor_id, sh.scoring_criteria_id, sh.created_at DESC, sh.id DESC
            """,
        resultSetMapping = "ScoringHistoryWithCriterionMapping"
    ),
    @NamedNativeQuery(
        name = "ScoringHistoryJpaEntity.findLatestPerCriterionForCompetitor",
        query = """
            SELECT DISTINCT ON (sh.scoring_criteria_id)
                sh.id                  AS id,
                sh.competition_id      AS competitionId,
                sh.competitor_id       AS competitorId,
                sh.scoring_criteria_id AS scoringCriterionId,
                sc.name                AS scoringCriterionName,
                sh.points_given        AS pointsGiven,
                sh.created_at          AS createdAt
            FROM scoring_history sh
            LEFT JOIN scoring_criteria sc ON sc.id = sh.scoring_criteria_id
            WHERE sh.competition_id = :competitionId
              AND sh.competitor_id = :competitorId
            ORDER BY sh.scoring_criteria_id, sh.created_at DESC, sh.id DESC
            """,
        resultSetMapping = "ScoringHistoryWithCriterionMapping"
    )
})
@SqlResultSetMappings({
    @SqlResultSetMapping(
        name = "ScoringDashboardRowMapping",
        classes = @ConstructorResult(
            targetClass = ScoringDashboardRowProjection.class,
            columns = {
                @ColumnResult(name = "competitorId", type = Integer.class),
                @ColumnResult(name = "timestamp", type = Timestamp.class),
                @ColumnResult(name = "runningTotal", type = Double.class)
            }
        )
    ),
    @SqlResultSetMapping(
        name = "ScoringHistoryWithCriterionMapping",
        classes = @ConstructorResult(
            targetClass = ScoringHistoryWithCriterionProjection.class,
            columns = {
                @ColumnResult(name = "id", type = Integer.class),
                @ColumnResult(name = "competitionId", type = Integer.class),
                @ColumnResult(name = "competitorId", type = Integer.class),
                @ColumnResult(name = "scoringCriterionId", type = Integer.class),
                @ColumnResult(name = "scoringCriterionName", type = String.class),
                @ColumnResult(name = "pointsGiven", type = Double.class),
                @ColumnResult(name = "createdAt", type = Timestamp.class)
            }
        )
    )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringHistoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "competition_id")
    private Integer competitionId;

    @Column(name = "competitor_id")
    private Integer competitorId;

    @Column(name = "scoring_criteria_id")
    private Integer scoringCriteriaId;

    @Column(name = "points_given")
    private Double pointsGiven;

    @Column(name = "result")
    private String result;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        ScoringHistoryJpaEntity that = (ScoringHistoryJpaEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
            : getClass().hashCode();
    }
}
