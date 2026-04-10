package com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionOrganizerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionOrganizerJpaRepository extends JpaRepository<CompetitionOrganizerJpaEntity, Integer> {
    Optional<CompetitionOrganizerJpaEntity> findByUsername(String username);
}

