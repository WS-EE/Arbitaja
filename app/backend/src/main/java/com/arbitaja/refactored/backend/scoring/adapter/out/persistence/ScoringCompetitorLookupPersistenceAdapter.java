package com.arbitaja.refactored.backend.scoring.adapter.out.persistence;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.mapper.ScoringPersistenceMapper;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository.ScoringCompetitorLookupJpaRepository;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetitor;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitorLookupPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter that loads competitor projections for scoring use cases.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ScoringCompetitorLookupPersistenceAdapter implements ScoringCompetitorLookupPort {

    private final ScoringCompetitorLookupJpaRepository repository;
    private final ScoringPersistenceMapper mapper;

    @Override
    public Optional<ScoringCompetitor> findById(@NonNull Integer competitorId) {
        return repository.findById(competitorId).map(mapper::toDomain);
    }

    @Override
    public List<ScoringCompetitor> findByCompetitionId(@NonNull Integer competitionId) {
        return repository.findByCompetitionId(competitionId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean isCompetitorInCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId) {
        return repository.existsCompetitorInCompetition(competitionId, competitorId);
    }
}
