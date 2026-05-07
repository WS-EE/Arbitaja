package com.arbitaja.refactored.backend.scoring.core.application.criterion;

import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.ManageScoringCriterionUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.CompetitionScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.ScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageScoringCriterionServiceTest {

    @Mock
    private ScoringCriterionRepositoryPort scoringCriterionRepository;

    @Mock
    private CompetitionScoringCriterionRepositoryPort competitionCriterionRepository;

    @Mock
    private ScoringCompetitionLookupPort competitionLookup;

    @InjectMocks
    private ManageScoringCriterionService service;

    @Test
    void createScoringCriterionPersistsAndReturnsCriterion() {
        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command =
            ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
                .name("speed")
                .totalPoints(10.0)
                .build();
        ScoringCriterion saved = ScoringCriterion.builder().id(11).name("speed").totalPoints(10.0).build();

        when(scoringCriterionRepository.save(any(ScoringCriterion.class))).thenReturn(saved);

        ScoringCriterion result = service.createScoringCriterion(command);

        assertEquals(11, result.getId());
        assertEquals("speed", result.getName());
        verify(competitionCriterionRepository, never()).linkCriterionToCompetition(any(), any());
    }

    @Test
    void createScoringCriterionLinksToCompetitionWhenIdProvided() {
        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command =
            ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
                .name("speed")
                .totalPoints(10.0)
                .competitionId(5)
                .build();
        ScoringCriterion saved = ScoringCriterion.builder().id(11).name("speed").totalPoints(10.0).build();

        when(competitionLookup.existsById(5)).thenReturn(true);
        when(scoringCriterionRepository.save(any(ScoringCriterion.class))).thenReturn(saved);

        service.createScoringCriterion(command);

        verify(competitionCriterionRepository).linkCriterionToCompetition(5, 11);
    }

    @Test
    void createScoringCriterionThrowsWhenCompetitionMissing() {
        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command =
            ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
                .name("speed")
                .totalPoints(10.0)
                .competitionId(404)
                .build();
        when(competitionLookup.existsById(404)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.createScoringCriterion(command));
        verify(scoringCriterionRepository, never()).save(any());
    }

    @Test
    void createScoringCriterionThrowsWhenTemplateMissing() {
        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command =
            ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
                .name("speed")
                .totalPoints(10.0)
                .criteriaTemplateId(99)
                .build();
        when(scoringCriterionRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.createScoringCriterion(command));
        verify(scoringCriterionRepository, never()).save(any());
    }

    @Test
    void updateScoringCriterionUpdatesFields() {
        ScoringCriterion existing = ScoringCriterion.builder().id(7).name("old").totalPoints(10.0).build();
        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command =
            ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
                .name("renamed")
                .description("updated")
                .totalPoints(20.0)
                .build();

        when(scoringCriterionRepository.findById(7)).thenReturn(Optional.of(existing));
        ArgumentCaptor<ScoringCriterion> savedCaptor = ArgumentCaptor.forClass(ScoringCriterion.class);
        when(scoringCriterionRepository.save(savedCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        ScoringCriterion result = service.updateScoringCriterion(7, command);

        assertEquals("renamed", result.getName());
        assertEquals("updated", result.getDescription());
        assertEquals(20.0, result.getTotalPoints());
        assertEquals("renamed", savedCaptor.getValue().getName());
    }

    @Test
    void updateScoringCriterionThrowsWhenMissing() {
        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command =
            ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
                .name("x")
                .totalPoints(1.0)
                .build();
        when(scoringCriterionRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateScoringCriterion(404, command));
    }

    @Test
    void deleteScoringCriterionThrowsWhenMissing() {
        when(scoringCriterionRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteScoringCriterion(404));
        verify(scoringCriterionRepository, never()).deleteById(any());
    }

    @Test
    void deleteScoringCriterionDelegatesToRepositoryWhenFound() {
        ScoringCriterion existing = ScoringCriterion.builder().id(7).build();
        when(scoringCriterionRepository.findById(7)).thenReturn(Optional.of(existing));

        service.deleteScoringCriterion(7);

        verify(scoringCriterionRepository).deleteById(7);
    }

    @Test
    void addScoringCriterionToCompetitionLinksWhenBothExist() {
        when(competitionLookup.existsById(1)).thenReturn(true);
        when(scoringCriterionRepository.findById(2)).thenReturn(Optional.of(ScoringCriterion.builder().id(2).build()));

        service.addScoringCriterionToCompetition(1, 2);

        verify(competitionCriterionRepository).linkCriterionToCompetition(1, 2);
    }

    @Test
    void addScoringCriterionToCompetitionThrowsWhenCompetitionMissing() {
        when(competitionLookup.existsById(1)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.addScoringCriterionToCompetition(1, 2));
    }

    @Test
    void addScoringCriterionToCompetitionThrowsWhenCriterionMissing() {
        when(competitionLookup.existsById(1)).thenReturn(true);
        when(scoringCriterionRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addScoringCriterionToCompetition(1, 2));
    }
}
