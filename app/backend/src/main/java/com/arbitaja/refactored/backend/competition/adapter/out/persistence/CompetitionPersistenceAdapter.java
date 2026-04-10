package com.arbitaja.refactored.backend.competition.adapter.out.persistence;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionOrganizerJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper.CompetitionPersistenceMapper;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitionJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitionOrganizerJpaRepository;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionCompetitorQueryPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter for competition aggregate storage.
 */
@Component
@RequiredArgsConstructor
public class CompetitionPersistenceAdapter implements CompetitionRepositoryPort {

    private final CompetitionJpaRepository competitionRepository;
    private final CompetitionOrganizerJpaRepository organizerRepository;
    private final CompetitionCompetitorQueryPort competitionCompetitorQuery;
    private final CompetitionPersistenceMapper mapper;

    @Override
    public List<Competition> findAll() {
        return competitionRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Competition> findById(@NonNull Integer id) {
        return competitionRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Competition> findByName(@NonNull String name) {
        return Optional.ofNullable(competitionRepository.findByName(name)).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(@NonNull String name) {
        return competitionRepository.findByName(name) != null;
    }

    @Override
    public boolean existsByNameAndIdNot(@NonNull String name, @NonNull Integer id) {
        CompetitionJpaEntity byName = competitionRepository.findByName(name);
        return byName != null && !id.equals(byName.getId());
    }

    @Override
    public Competition save(@NonNull Competition competition) {
        CompetitionOrganizerJpaEntity organizer = null;
        if (competition.getOrganizer() != null && competition.getOrganizer().getId() != null) {
            organizer = organizerRepository.findById(competition.getOrganizer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found for id: " + competition.getOrganizer().getId()));
        }

        CompetitionJpaEntity entity = mapper.toEntity(competition, organizer);
        CompetitionJpaEntity saved = competitionRepository.save(entity);
        return mapper.toDomain(saved, competitionCompetitorQuery.findByCompetitionId(saved.getId()));
    }

    @Override
    public void deleteById(@NonNull Integer id) {
        competitionRepository.deleteById(id);
    }
}

