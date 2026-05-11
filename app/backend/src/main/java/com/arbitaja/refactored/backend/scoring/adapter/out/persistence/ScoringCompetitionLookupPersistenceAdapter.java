package com.arbitaja.refactored.backend.scoring.adapter.out.persistence;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.mapper.ScoringPersistenceMapper;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository.ScoringCompetitionLookupJpaRepository;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetition;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Optional;

/**
 * Persistence adapter that loads competition projections for scoring use cases.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ScoringCompetitionLookupPersistenceAdapter implements ScoringCompetitionLookupPort {

    private final ScoringCompetitionLookupJpaRepository repository;
    private final ScoringPersistenceMapper mapper;

    @Override
    public Optional<ScoringCompetition> findById(@NonNull Integer competitionId) {
        return repository.findById(competitionId).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(@NonNull Integer competitionId) {
        return repository.existsById(competitionId);
    }
}
