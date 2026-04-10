package com.arbitaja.refactored.backend.competition.adapter.out.persistence;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper.CompetitionPersistenceMapper;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorJpaRepository;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionCompetitor;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionCompetitorQueryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Loads competition competitor summaries from competition persistence.
 */
@Component
@RequiredArgsConstructor
public class CompetitionCompetitorQueryPersistenceAdapter implements CompetitionCompetitorQueryPort {

    private final CompetitorJpaRepository competitorRepository;
    private final CompetitionPersistenceMapper mapper;

    @Override
    public Set<CompetitionCompetitor> findByCompetitionId(@NonNull Integer competitionId) {
        return mapper.toDomainCompetitors(competitorRepository.findByCompetitionId(competitionId));
    }
}

