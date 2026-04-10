package com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorCompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitorCompetitionJpaRepository extends JpaRepository<CompetitorCompetitionJpaEntity, Integer> {
    CompetitorCompetitionJpaEntity findByCompetitorAndCompetition(CompetitorJpaEntity competitor, CompetitionJpaEntity competition);
}

