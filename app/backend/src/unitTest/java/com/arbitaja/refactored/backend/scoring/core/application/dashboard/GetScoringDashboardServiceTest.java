package com.arbitaja.refactored.backend.scoring.core.application.dashboard;

import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitionScoringCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorDashboard;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CriterionResult;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetition;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetitor;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.CompetitionScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.history.ScoringDashboardQueryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitorLookupPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetScoringDashboardServiceTest {

    @Mock
    private ScoringCompetitionLookupPort competitionLookup;

    @Mock
    private ScoringCompetitorLookupPort competitorLookup;

    @Mock
    private CompetitionScoringCriterionRepositoryPort competitionCriterionRepository;

    @Mock
    private ScoringDashboardQueryPort scoringDashboardQuery;

    @InjectMocks
    private GetScoringDashboardService service;

    private ScoringCompetition competition() {
        return ScoringCompetition.builder()
            .id(1)
            .name("Final")
            .startTime(Timestamp.from(Instant.parse("2026-04-06T09:00:00Z")))
            .endTime(Timestamp.from(Instant.parse("2026-04-06T18:00:00Z")))
            .scoreShowtime(Timestamp.from(Instant.parse("2026-04-06T15:00:00Z")))
            .publishScores(true)
            .build();
    }

    private ScoringCompetitor competitor(int id, String alias) {
        return ScoringCompetitor.builder()
            .id(id)
            .alias(alias)
            .publicDisplayNameType(3)
            .build();
    }

    @Test
    void getDashboardComputesRunningTotalForEachCompetitor() {
        when(competitionLookup.findById(1)).thenReturn(Optional.of(competition()));
        when(competitorLookup.findByCompetitionId(1)).thenReturn(List.of(competitor(10, "alice"), competitor(20, "bob")));

        Timestamp t1 = Timestamp.from(Instant.parse("2026-04-06T10:00:00Z"));
        Timestamp t2 = Timestamp.from(Instant.parse("2026-04-06T11:00:00Z"));
        Timestamp t3 = Timestamp.from(Instant.parse("2026-04-06T12:00:00Z"));

        ScoringHistoryEntry alicePoint1 = ScoringHistoryEntry.builder()
            .competitorId(10).scoringCriterionId(100).pointsGiven(3.0).createdAt(t1).build();
        ScoringHistoryEntry alicePoint2 = ScoringHistoryEntry.builder()
            .competitorId(10).scoringCriterionId(101).pointsGiven(2.0).createdAt(t2).build();
        ScoringHistoryEntry aliceUpdate = ScoringHistoryEntry.builder()
            .competitorId(10).scoringCriterionId(100).pointsGiven(5.0).createdAt(t3).build();
        ScoringHistoryEntry bobPoint = ScoringHistoryEntry.builder()
            .competitorId(20).scoringCriterionId(100).pointsGiven(4.0).createdAt(t1).build();

        when(scoringDashboardQuery.findHistoryForCompetition(1, competition().getEndTime()))
            .thenReturn(List.of(alicePoint1, alicePoint2, aliceUpdate, bobPoint));

        var dashboard = service.getDashboard(1, true);

        assertEquals(2, dashboard.getCompetitors().size());
        CompetitorDashboard alice = dashboard.getCompetitors().stream()
            .filter(c -> c.getCompetitorId() == 10).findFirst().orElseThrow();
        assertEquals(7.0, alice.getTotalScore());
        assertEquals(3, alice.getResults().size());
        assertEquals(3.0, alice.getResults().get(0).getPointAmount());
        assertEquals(5.0, alice.getResults().get(1).getPointAmount());
        assertEquals(7.0, alice.getResults().get(2).getPointAmount());

        CompetitorDashboard bob = dashboard.getCompetitors().stream()
            .filter(c -> c.getCompetitorId() == 20).findFirst().orElseThrow();
        assertEquals(4.0, bob.getTotalScore());
    }

    @Test
    void getDashboardUsesScoreShowtimeWhenViewerIsNotAdmin() {
        ScoringCompetition competition = competition();
        when(competitionLookup.findById(1)).thenReturn(Optional.of(competition));
        when(competitorLookup.findByCompetitionId(1)).thenReturn(List.of());
        when(scoringDashboardQuery.findHistoryForCompetition(1, competition.getScoreShowtime()))
            .thenReturn(List.of());

        service.getDashboard(1, false);
    }

    @Test
    void getDashboardThrowsWhenCompetitionMissing() {
        when(competitionLookup.findById(404)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getDashboard(404, true));
    }

    @Test
    void getCriteriaResultsForCompetitionAggregatesLatestPerCompetitor() {
        when(competitionLookup.findById(1)).thenReturn(Optional.of(competition()));
        when(competitorLookup.findByCompetitionId(1)).thenReturn(List.of(competitor(10, "alice")));
        ScoringCriterion criterionA = ScoringCriterion.builder().id(100).name("ssh").totalPoints(5.0).build();
        ScoringCriterion criterionB = ScoringCriterion.builder().id(101).name("ftp").totalPoints(5.0).build();
        when(competitionCriterionRepository.findCriteriaForCompetition(1)).thenReturn(List.of(criterionA, criterionB));

        ScoringHistoryEntry latestA = ScoringHistoryEntry.builder()
            .competitorId(10).scoringCriterionId(100).scoringCriterionName("ssh").pointsGiven(4.0).build();
        when(scoringDashboardQuery.findLatestPerCompetitorAndCriterion(1)).thenReturn(List.of(latestA));

        CompetitionScoringCriteriaResults results = service.getCriteriaResultsForCompetition(1, true);

        CompetitorCriteriaResults aliceResults = results.getCompetitors().iterator().next();
        assertEquals(2, aliceResults.getCriteria().size());
        CriterionResult sshResult = aliceResults.getCriteria().stream()
            .filter(r -> r.getCriterionId() == 100).findFirst().orElseThrow();
        assertEquals(4.0, sshResult.getPoints());
        CriterionResult ftpResult = aliceResults.getCriteria().stream()
            .filter(r -> r.getCriterionId() == 101).findFirst().orElseThrow();
        assertEquals(0.0, ftpResult.getPoints());
    }

    @Test
    void getCriteriaResultsForCompetitionThrowsForbiddenWhenScoresUnpublishedAndViewerNotAdmin() {
        ScoringCompetition unpublished = ScoringCompetition.builder()
            .id(1).publishScores(false)
            .startTime(Timestamp.from(Instant.parse("2026-04-06T09:00:00Z")))
            .endTime(Timestamp.from(Instant.parse("2026-04-06T18:00:00Z")))
            .build();
        when(competitionLookup.findById(1)).thenReturn(Optional.of(unpublished));

        assertThrows(ForbiddenException.class, () -> service.getCriteriaResultsForCompetition(1, false));
    }

    @Test
    void getCriteriaResultsForCompetitorReturnsZeroForMissingCriteria() {
        when(competitionLookup.findById(1)).thenReturn(Optional.of(competition()));
        when(competitorLookup.findById(10)).thenReturn(Optional.of(competitor(10, "alice")));
        ScoringCriterion criterion = ScoringCriterion.builder().id(100).name("ssh").totalPoints(5.0).build();
        when(competitionCriterionRepository.findCriteriaForCompetition(1)).thenReturn(List.of(criterion));
        when(scoringDashboardQuery.findLatestPerCriterionForCompetitor(1, 10)).thenReturn(List.of());

        CompetitorCriteriaResults result = service.getCriteriaResultsForCompetitor(1, 10, true);

        assertEquals(1, result.getCriteria().size());
        assertEquals(0.0, result.getCriteria().iterator().next().getPoints());
    }

    @Test
    void getCriteriaResultsForCompetitorThrowsWhenCompetitorMissing() {
        when(competitionLookup.findById(1)).thenReturn(Optional.of(competition()));
        when(competitorLookup.findById(404)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getCriteriaResultsForCompetitor(1, 404, true));
    }
}
