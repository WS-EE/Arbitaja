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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageCompetitionServiceTest {

    @Mock
    private CompetitionRepositoryPort competitionRepository;

    @Mock
    private CompetitionOrganizerLookupPort organizerLookup;

    @Mock
    private CompetitionMembershipPort competitionMembership;

    @Mock
    private CompetitionCompetitorQueryPort competitionCompetitorQuery;

    @InjectMocks
    private ManageCompetitionService service;

    @Test
    void createCompetitionCreatesEntityWhenValid() {
        ManageCompetitionUseCase.UpsertCompetitionCommand command = ManageCompetitionUseCase.UpsertCompetitionCommand.builder()
            .name("Noor Meister")
            .startTime(Timestamp.valueOf("2026-04-06 09:00:00"))
            .endTime(Timestamp.valueOf("2026-04-06 18:00:00"))
            .organizerId(7)
            .publishScores(true)
            .build();

        CompetitionOrganizer organizer = CompetitionOrganizer.builder().id(7).username("admin").build();
        Competition saved = Competition.builder().id(11).name("Noor Meister").organizer(organizer).build();

        when(competitionRepository.existsByName("Noor Meister")).thenReturn(false);
        when(organizerLookup.findById(7)).thenReturn(Optional.of(organizer));
        when(competitionRepository.save(any(Competition.class))).thenReturn(saved);

        Competition result = service.createCompetition(command);

        assertEquals(11, result.getId());
        assertEquals("Noor Meister", result.getName());
        assertEquals(7, result.getOrganizer().getId());
    }

    @Test
    void createCompetitionThrowsOnDuplicateName() {
        ManageCompetitionUseCase.UpsertCompetitionCommand command = ManageCompetitionUseCase.UpsertCompetitionCommand.builder()
            .name("Taken")
            .startTime(Timestamp.valueOf("2026-04-06 09:00:00"))
            .endTime(Timestamp.valueOf("2026-04-06 18:00:00"))
            .organizerId(7)
            .build();

        when(competitionRepository.existsByName("Taken")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.createCompetition(command));
    }

    @Test
    void updateCompetitionThrowsWhenCompetitionMissing() {
        ManageCompetitionUseCase.UpsertCompetitionCommand command = ManageCompetitionUseCase.UpsertCompetitionCommand.builder()
            .name("Updated")
            .startTime(Timestamp.valueOf("2026-04-06 09:00:00"))
            .endTime(Timestamp.valueOf("2026-04-06 18:00:00"))
            .build();

        when(competitionRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateCompetition(404, command));
    }

    @Test
    void addCompetitorToCompetitionAddsLinkAndReturnsEnrichedCompetition() {
        Competition competition = Competition.builder().id(9).name("Spring Cup").build();
        Set<CompetitionCompetitor> competitors = Set.of(
            CompetitionCompetitor.builder().id(3).fullName("Bob").alias("B").build()
        );

        when(competitionRepository.findById(9)).thenReturn(Optional.of(competition), Optional.of(competition));
        when(competitionMembership.existsCompetitor(3)).thenReturn(true);
        when(competitionMembership.isCompetitorInCompetition(9, 3)).thenReturn(false);
        when(competitionCompetitorQuery.findByCompetitionId(9)).thenReturn(competitors);

        Competition result = service.addCompetitorToCompetition(9, 3);

        assertEquals(1, result.getCompetitors().size());
        verify(competitionMembership).addCompetitorToCompetition(9, 3);
    }

    @Test
    void addCompetitorToCompetitionThrowsWhenCompetitorAlreadyAssigned() {
        Competition competition = Competition.builder().id(9).name("Spring Cup").build();
        when(competitionRepository.findById(9)).thenReturn(Optional.of(competition));
        when(competitionMembership.existsCompetitor(3)).thenReturn(true);
        when(competitionMembership.isCompetitorInCompetition(9, 3)).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> service.addCompetitorToCompetition(9, 3));
    }

    @Test
    void removeCompetitorFromCompetitionThrowsWhenLinkMissing() {
        Competition competition = Competition.builder().id(9).name("Spring Cup").build();
        when(competitionRepository.findById(9)).thenReturn(Optional.of(competition));
        when(competitionMembership.existsCompetitor(3)).thenReturn(true);
        when(competitionMembership.isCompetitorInCompetition(9, 3)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.removeCompetitorFromCompetition(9, 3));
    }

    @Test
    void overwriteCompetitionCompetitorsReplacesMembershipUsingSetDifference() {
        Competition competition = Competition.builder().id(9).name("Spring Cup").build();
        Set<CompetitionCompetitor> currentCompetitors = Set.of(
            CompetitionCompetitor.builder().id(1).alias("A").build(),
            CompetitionCompetitor.builder().id(2).alias("B").build()
        );
        Set<CompetitionCompetitor> updatedCompetitors = Set.of(
            CompetitionCompetitor.builder().id(2).alias("B").build(),
            CompetitionCompetitor.builder().id(3).alias("C").build()
        );

        when(competitionRepository.findById(9)).thenReturn(Optional.of(competition), Optional.of(competition));
        when(competitionCompetitorQuery.findByCompetitionId(9)).thenReturn(currentCompetitors, updatedCompetitors);
        when(competitionMembership.existsCompetitor(2)).thenReturn(true);
        when(competitionMembership.existsCompetitor(3)).thenReturn(true);

        Competition result = service.overwriteCompetitionCompetitors(9, java.util.List.of(2, 3));

        verify(competitionMembership).removeCompetitorFromCompetition(9, 1);
        verify(competitionMembership).addCompetitorToCompetition(9, 3);
        assertEquals(2, result.getCompetitors().size());
    }

    @Test
    void overwriteCompetitionCompetitorsThrowsWhenRequestedCompetitorDoesNotExist() {
        Competition competition = Competition.builder().id(9).name("Spring Cup").build();
        when(competitionRepository.findById(9)).thenReturn(Optional.of(competition));
        when(competitionMembership.existsCompetitor(999)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.overwriteCompetitionCompetitors(9, java.util.List.of(999)));
        verify(competitionMembership, never()).addCompetitorToCompetition(anyInt(), anyInt());
        verify(competitionMembership, never()).removeCompetitorFromCompetition(anyInt(), anyInt());
    }
}

