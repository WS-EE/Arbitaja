package com.arbitaja.refactored.backend.competition.adapter.out.persistence;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper.CompetitionPersistenceMapper;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitionOrganizerJpaRepository;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionOrganizer;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionOrganizerLookupPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Optional;

/**
 * Resolves organizers from competition-facing persistence projection.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitionOrganizerLookupPersistenceAdapter implements CompetitionOrganizerLookupPort {

    private final CompetitionOrganizerJpaRepository organizerRepository;
    private final CompetitionPersistenceMapper mapper;

    @Override
    public Optional<CompetitionOrganizer> findById(@NonNull Integer id) {
        return organizerRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<CompetitionOrganizer> findByUsername(@NonNull String username) {
        return organizerRepository.findByUsername(username).map(mapper::toDomain);
    }
}

