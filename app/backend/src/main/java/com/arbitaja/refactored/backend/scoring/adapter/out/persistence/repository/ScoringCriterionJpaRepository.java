package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCriterionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoringCriterionJpaRepository extends JpaRepository<ScoringCriterionJpaEntity, Integer> {
    Page<ScoringCriterionJpaEntity> findByNameContainingIgnoreCase(String search, Pageable pageable);
}
