package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCompetitorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScoringCompetitorLookupJpaRepository extends JpaRepository<ScoringCompetitorJpaEntity, Integer> {

    @Query(value = """
        SELECT c.id AS id,
               c.public_display_name_type AS public_display_name_type,
               c.alias AS alias,
               c.personal_data_id AS personal_data_id
        FROM competition co
        JOIN competitor_competition cc ON co.id = cc.competition_id
        JOIN competitor c ON cc.competitor_id = c.id
        WHERE co.id = :competitionId
    """, nativeQuery = true)
    List<ScoringCompetitorJpaEntity> findByCompetitionId(@Param("competitionId") Integer competitionId);

    @Query(value = """
        SELECT (COUNT(*) > 0)
        FROM competitor_competition cc
        WHERE cc.competition_id = :competitionId AND cc.competitor_id = :competitorId
    """, nativeQuery = true)
    boolean existsCompetitorInCompetition(@Param("competitionId") Integer competitionId, @Param("competitorId") Integer competitorId);
}
