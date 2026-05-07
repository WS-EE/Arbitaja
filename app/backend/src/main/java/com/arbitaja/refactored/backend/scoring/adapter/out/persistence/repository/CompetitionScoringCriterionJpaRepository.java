package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.CompetitionScoringCriterionJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCriterionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompetitionScoringCriterionJpaRepository extends JpaRepository<CompetitionScoringCriterionJpaEntity, Integer> {

    @Query("""
        SELECT sc FROM ScoringCriterionJpaEntity sc
        WHERE sc.id IN (
            SELECT csc.criteriaId FROM CompetitionScoringCriterionJpaEntity csc
            WHERE csc.competitionId = :competitionId
        )
    """)
    List<ScoringCriterionJpaEntity> findCriteriaByCompetitionId(Integer competitionId);
}
