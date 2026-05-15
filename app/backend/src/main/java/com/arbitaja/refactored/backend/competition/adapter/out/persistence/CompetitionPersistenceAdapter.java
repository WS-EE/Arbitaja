package com.arbitaja.refactored.backend.competition.adapter.out.persistence;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionOrganizerJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper.CompetitionPersistenceMapper;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitionJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitionOrganizerJpaRepository;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionCompetitorQueryPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.sql.Timestamp;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter for competition aggregate storage.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
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
        CompetitionOrganizerJpaEntity organizer = resolveOrganizer(competition);

        CompetitionJpaEntity persisted = competition.getId() == null
            ? insert(competition, organizer)
            : update(competition, organizer);

        return mapper.toDomain(persisted, competitionCompetitorQuery.findByCompetitionId(persisted.getId()));
    }

    @Override
    public void deleteById(@NonNull Integer id) {
        competitionRepository.deleteById(id);
    }

    @Override
    public Page<Competition> findPaged(String search, Pageable pageable) {
        return findPaged(search, null, pageable);
    }

    @Override
    public Page<Competition> findPaged(String search, String status, Pageable pageable) {
        String s = search == null ? "" : search;
        String st = (status == null || status.isBlank()) ? "ALL" : status.toUpperCase();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return competitionRepository.findBySearchAndStatus(s, st, now, pageable)
            .map(mapper::toDomain);
    }

    private CompetitionOrganizerJpaEntity resolveOrganizer(Competition competition) {
        if (competition.getOrganizer() == null || competition.getOrganizer().getId() == null) {
            return null;
        }
        return organizerRepository.findById(competition.getOrganizer().getId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Organizer not found for id: " + competition.getOrganizer().getId()));
    }

    private CompetitionJpaEntity insert(Competition competition, CompetitionOrganizerJpaEntity organizer) {
        CompetitionJpaEntity entity = mapper.toEntity(competition, organizer);
        return competitionRepository.save(entity);
    }

    /**
     * Loads the existing row and patches only the scalar columns. Avoids re-attaching the
     * empty {@code competitorCompetitions} collection from a freshly built entity, which
     * previously triggered orphan-removal and deleted every competitor link.
     */
    private CompetitionJpaEntity update(Competition competition, CompetitionOrganizerJpaEntity organizer) {
        CompetitionJpaEntity existing = competitionRepository.findById(competition.getId())
            .orElseThrow(() -> EntityNotFoundException.competition(competition.getId()));

        existing.setName(competition.getName());
        existing.setStartTime(competition.getStartTime());
        existing.setEndTime(competition.getEndTime());
        existing.setScoreShowtime(competition.getScoreShowtime());
        existing.setPublishScores(competition.getPublishScores());
        existing.setOrganizer(organizer);

        return competitionRepository.save(existing);
    }
}
