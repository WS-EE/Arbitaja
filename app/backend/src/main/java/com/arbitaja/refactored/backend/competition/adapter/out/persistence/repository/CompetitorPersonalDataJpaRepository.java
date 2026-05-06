package com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorPersonalDataJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitorPersonalDataJpaRepository extends JpaRepository<CompetitorPersonalDataJpaEntity, Integer> {
}

