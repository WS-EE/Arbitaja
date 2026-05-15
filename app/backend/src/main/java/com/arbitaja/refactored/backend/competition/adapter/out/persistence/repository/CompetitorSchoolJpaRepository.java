package com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorSchoolJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitorSchoolJpaRepository extends JpaRepository<CompetitorSchoolJpaEntity, Integer> {
    Optional<CompetitorSchoolJpaEntity> findByName(String name);

    Page<CompetitorSchoolJpaEntity> findByNameContainingIgnoreCase(String search, Pageable pageable);
}

