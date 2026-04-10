package com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CompetitorJpaRepository extends JpaRepository<CompetitorJpaEntity, Integer> {

    @Query(value = """
    SELECT c.id AS id, c.public_display_name_type AS public_display_name_type, c.alias AS alias, c.personal_data_id AS personal_data_id
    FROM competition co
    JOIN competitor_competition cc ON co.id = cc.competition_id
    JOIN competitor c ON cc.competitor_id = c.id
    WHERE co.id = :competitionId
""", nativeQuery = true)
    Set<CompetitorJpaEntity> findByCompetitionId(Integer competitionId);

    CompetitorJpaEntity findByAlias(String alias);
}

