package com.arbitaja.refactored.backend.scoring.core.application.history;

import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.ValidationException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetition;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetitor;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import com.arbitaja.refactored.backend.scoring.core.port.in.history.RecordScoringHistoryUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.ScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.history.ScoringHistoryRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitorLookupPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordScoringHistoryServiceTest {

    @Mock
    private ScoringCompetitionLookupPort competitionLookup;

    @Mock
    private ScoringCompetitorLookupPort competitorLookup;

    @Mock
    private ScoringCriterionRepositoryPort scoringCriterionRepository;

    @Mock
    private ScoringHistoryRepositoryPort scoringHistoryRepository;

    private RecordScoringHistoryService service;

    private final Instant fixedNow = Instant.parse("2026-04-06T12:00:00Z");

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(fixedNow, ZoneOffset.UTC);
        service = new RecordScoringHistoryService(
            competitionLookup, competitorLookup, scoringCriterionRepository, scoringHistoryRepository, fixedClock);
    }

    private RecordScoringHistoryUseCase.RecordScoringCommand command(double points) {
        return RecordScoringHistoryUseCase.RecordScoringCommand.builder()
            .competitionId(1)
            .competitorId(2)
            .scoringCriterionId(3)
            .points(points)
            .build();
    }

    private ScoringCompetition activeCompetition() {
        return ScoringCompetition.builder()
            .id(1)
            .name("Final")
            .startTime(Timestamp.from(fixedNow.minusSeconds(3600)))
            .endTime(Timestamp.from(fixedNow.plusSeconds(3600)))
            .build();
    }

    @Test
    void recordScorePersistsEntryWithProvidedClock() {
        ScoringCriterion criterion = ScoringCriterion.builder().id(3).name("ssh").totalPoints(10.0).build();
        when(competitionLookup.findById(1)).thenReturn(Optional.of(activeCompetition()));
        when(scoringCriterionRepository.findById(3)).thenReturn(Optional.of(criterion));
        when(competitorLookup.findById(2)).thenReturn(Optional.of(ScoringCompetitor.builder().id(2).build()));
        when(competitorLookup.isCompetitorInCompetition(1, 2)).thenReturn(true);
        when(scoringHistoryRepository.save(any(ScoringHistoryEntry.class))).thenAnswer(inv -> {
            ScoringHistoryEntry e = inv.getArgument(0);
            e.setId(99);
            return e;
        });

        ScoringHistoryEntry result = service.recordScore(command(5.0));

        assertEquals(99, result.getId());
        assertEquals(5.0, result.getPointsGiven());
        assertEquals(Timestamp.from(fixedNow), result.getCreatedAt());

        ArgumentCaptor<ScoringHistoryEntry> captor = ArgumentCaptor.forClass(ScoringHistoryEntry.class);
        verify(scoringHistoryRepository).save(captor.capture());
        assertEquals("ssh", captor.getValue().getScoringCriterionName());
    }

    @Test
    void recordScoreThrowsWhenCompetitionMissing() {
        when(competitionLookup.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.recordScore(command(5.0)));
        verify(scoringHistoryRepository, never()).save(any());
    }

    @Test
    void recordScoreThrowsWhenCriterionMissing() {
        when(competitionLookup.findById(1)).thenReturn(Optional.of(activeCompetition()));
        when(scoringCriterionRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.recordScore(command(5.0)));
    }

    @Test
    void recordScoreThrowsWhenPointsExceedTotal() {
        ScoringCriterion criterion = ScoringCriterion.builder().id(3).totalPoints(10.0).build();
        when(competitionLookup.findById(1)).thenReturn(Optional.of(activeCompetition()));
        when(scoringCriterionRepository.findById(3)).thenReturn(Optional.of(criterion));

        assertThrows(ValidationException.class, () -> service.recordScore(command(11.0)));
    }

    @Test
    void recordScoreThrowsWhenPointsAreNegative() {
        ScoringCriterion criterion = ScoringCriterion.builder().id(3).totalPoints(10.0).build();
        when(competitionLookup.findById(1)).thenReturn(Optional.of(activeCompetition()));
        when(scoringCriterionRepository.findById(3)).thenReturn(Optional.of(criterion));

        assertThrows(ValidationException.class, () -> service.recordScore(command(-1.0)));
    }

    @Test
    void recordScoreThrowsWhenCompetitorMissing() {
        ScoringCriterion criterion = ScoringCriterion.builder().id(3).totalPoints(10.0).build();
        when(competitionLookup.findById(1)).thenReturn(Optional.of(activeCompetition()));
        when(scoringCriterionRepository.findById(3)).thenReturn(Optional.of(criterion));
        when(competitorLookup.findById(2)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.recordScore(command(5.0)));
    }

    @Test
    void recordScoreThrowsWhenCompetitorNotInCompetition() {
        ScoringCriterion criterion = ScoringCriterion.builder().id(3).totalPoints(10.0).build();
        when(competitionLookup.findById(1)).thenReturn(Optional.of(activeCompetition()));
        when(scoringCriterionRepository.findById(3)).thenReturn(Optional.of(criterion));
        when(competitorLookup.findById(2)).thenReturn(Optional.of(ScoringCompetitor.builder().id(2).build()));
        when(competitorLookup.isCompetitorInCompetition(1, 2)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.recordScore(command(5.0)));
    }

    @Test
    void recordScoreThrowsWhenCompetitionNotActive() {
        ScoringCompetition expired = ScoringCompetition.builder()
            .id(1)
            .startTime(Timestamp.from(fixedNow.minusSeconds(7200)))
            .endTime(Timestamp.from(fixedNow.minusSeconds(3600)))
            .build();
        ScoringCriterion criterion = ScoringCriterion.builder().id(3).totalPoints(10.0).build();
        when(competitionLookup.findById(1)).thenReturn(Optional.of(expired));
        when(scoringCriterionRepository.findById(3)).thenReturn(Optional.of(criterion));
        when(competitorLookup.findById(2)).thenReturn(Optional.of(ScoringCompetitor.builder().id(2).build()));
        when(competitorLookup.isCompetitorInCompetition(1, 2)).thenReturn(true);

        assertThrows(ValidationException.class, () -> service.recordScore(command(5.0)));
    }
}
