package com.arbitaja.refactored.backend.competition.core.application.competition;

import com.arbitaja.refactored.backend.competition.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionCompetitor;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionOrganizer;
import com.arbitaja.refactored.backend.competition.core.port.in.competition.ManageCompetitionUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionCompetitorQueryPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionMembershipPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionOrganizerLookupPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionRepositoryPort;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Application service for competition write operations.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ManageCompetitionService implements ManageCompetitionUseCase {

    private final CompetitionRepositoryPort competitionRepository;
    private final CompetitionOrganizerLookupPort organizerLookup;
    private final CompetitionMembershipPort competitionMembership;
    private final CompetitionCompetitorQueryPort competitionCompetitorQuery;

    @Override
    @Transactional
    public Competition createCompetition(@NonNull UpsertCompetitionCommand command) {
        if (competitionRepository.existsByName(command.getName())) {
            throw DuplicateEntityException.competitionByName(command.getName());
        }

        Competition competition = toDomain(null, command);
        return competitionRepository.save(competition);
    }

    @Override
    @Transactional
    public Competition updateCompetition(@NonNull Integer id, @NonNull UpsertCompetitionCommand command) {
        if (competitionRepository.findById(id).isEmpty()) {
            throw EntityNotFoundException.competition(id);
        }

        if (competitionRepository.existsByNameAndIdNot(command.getName(), id)) {
            throw DuplicateEntityException.competitionByName(command.getName());
        }

        Competition competition = toDomain(id, command);
        return competitionRepository.save(competition);
    }

    @Override
    @Transactional
    public Competition addCompetitorToCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId) {
        assertCompetitionExists(competitionId);
        if (!competitionMembership.existsCompetitor(competitorId)) {
            throw EntityNotFoundException.competitor(competitorId);
        }
        if (competitionMembership.isCompetitorInCompetition(competitionId, competitorId)) {
            throw DuplicateEntityException.competitorAlreadyInCompetition(competitionId, competitorId);
        }

        competitionMembership.addCompetitorToCompetition(competitionId, competitorId);
        return fetchCompetitionWithCompetitors(competitionId);
    }

    @Override
    @Transactional
    public Competition removeCompetitorFromCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId) {
        assertCompetitionExists(competitionId);
        if (!competitionMembership.existsCompetitor(competitorId)) {
            throw EntityNotFoundException.competitor(competitorId);
        }
        if (!competitionMembership.isCompetitorInCompetition(competitionId, competitorId)) {
            throw EntityNotFoundException.competitorInCompetition(competitionId, competitorId);
        }

        competitionMembership.removeCompetitorFromCompetition(competitionId, competitorId);
        return fetchCompetitionWithCompetitors(competitionId);
    }

    @Override
    @Transactional
    public Competition overwriteCompetitionCompetitors(@NonNull Integer competitionId, List<Integer> competitorIds) {
        assertCompetitionExists(competitionId);

        Set<Integer> requestedCompetitorIds = competitorIds == null
            ? Set.of()
            : new LinkedHashSet<>(competitorIds);

        requestedCompetitorIds.forEach(competitorId -> {
            if (!competitionMembership.existsCompetitor(competitorId)) {
                throw EntityNotFoundException.competitor(competitorId);
            }
        });

        Set<Integer> currentCompetitorIds = competitionCompetitorQuery.findByCompetitionId(competitionId).stream()
            .map(CompetitionCompetitor::getId)
            .filter(Objects::nonNull)
            .collect(java.util.stream.Collectors.toSet());

        currentCompetitorIds.stream()
            .filter(currentId -> !requestedCompetitorIds.contains(currentId))
            .forEach(competitorId -> competitionMembership.removeCompetitorFromCompetition(competitionId, competitorId));

        requestedCompetitorIds.stream()
            .filter(requestedId -> !currentCompetitorIds.contains(requestedId))
            .forEach(competitorId -> competitionMembership.addCompetitorToCompetition(competitionId, competitorId));

        return fetchCompetitionWithCompetitors(competitionId);
    }

    @Override
    @Transactional
    public void deleteCompetition(@NonNull Integer id) {
        assertCompetitionExists(id);
        competitionRepository.deleteById(id);
    }

    private void assertCompetitionExists(Integer id) {
        if (competitionRepository.findById(id).isEmpty()) {
            throw EntityNotFoundException.competition(id);
        }
    }

    private Competition fetchCompetitionWithCompetitors(Integer competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> EntityNotFoundException.competition(competitionId));
        competition.setCompetitors(competitionCompetitorQuery.findByCompetitionId(competitionId));
        return competition;
    }

    private Competition toDomain(Integer id, UpsertCompetitionCommand command) {
        CompetitionOrganizer organizer = resolveOrganizer(command);

        return Competition.builder()
            .id(id)
            .name(command.getName())
            .startTime(command.getStartTime())
            .endTime(command.getEndTime())
            .scoreShowtime(command.getScoreShowtime())
            .publishScores(command.getPublishScores())
            .organizer(organizer)
            .build();
    }

    private CompetitionOrganizer resolveOrganizer(UpsertCompetitionCommand command) {
        if (command.getOrganizerId() != null) {
            return organizerLookup.findById(command.getOrganizerId())
                .orElseThrow(() -> EntityNotFoundException.organizer(command.getOrganizerId()));
        }

        throw new IllegalArgumentException("OrganizerId must be provided");
    }
}

