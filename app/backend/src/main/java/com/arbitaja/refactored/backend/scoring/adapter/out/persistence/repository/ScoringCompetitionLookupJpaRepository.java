package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCompetitionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoringCompetitionLookupJpaRepository extends JpaRepository<ScoringCompetitionJpaEntity, Integer> {
}
