package com.arbitaja.refactored.backend.scoring.core.application.dashboard;

import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitionScoringCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorDashboard;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CriterionResult;
import com.arbitaja.refactored.backend.scoring.core.domain.model.DashboardResultPoint;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetition;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetitor;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringDashboard;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import com.arbitaja.refactored.backend.scoring.core.port.in.dashboard.GetScoringDashboardUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.CompetitionScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.history.ScoringDashboardQueryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitorLookupPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Application service for scoring dashboard projections.
 *
 * <p>This service replaces the legacy N+1 dashboard implementation with a
 * single-query strategy: the persistence adapter loads every scoring history row
 * for the competition (and per-criterion latest rows for the criteria result view),
 * and this service walks the rows once to compute running totals.</p>
 */
@Service
@RequiredArgsConstructor
public class GetScoringDashboardService implements GetScoringDashboardUseCase {

    private final ScoringCompetitionLookupPort competitionLookup;
    private final ScoringCompetitorLookupPort competitorLookup;
    private final CompetitionScoringCriterionRepositoryPort competitionCriterionRepository;
    private final ScoringDashboardQueryPort scoringDashboardQuery;

    @Override
    public ScoringDashboard getDashboard(@NonNull Integer competitionId, boolean viewerIsAdmin) {
        ScoringCompetition competition = competitionLookup.findById(competitionId)
            .orElseThrow(() -> EntityNotFoundException.competition(competitionId));

        Timestamp cutoff = viewerIsAdmin ? competition.getEndTime() : competition.getScoreShowtime();
        if (cutoff == null) {
            cutoff = competition.getEndTime();
        }

        List<ScoringCompetitor> competitors = competitorLookup.findByCompetitionId(competitionId);
        List<ScoringHistoryEntry> history = scoringDashboardQuery.findHistoryForCompetition(competitionId, cutoff);

        Map<Integer, List<ScoringHistoryEntry>> historyByCompetitor = new HashMap<>();
        for (ScoringHistoryEntry entry : history) {
            historyByCompetitor.computeIfAbsent(entry.getCompetitorId(), id -> new ArrayList<>()).add(entry);
        }

        Set<CompetitorDashboard> competitorDashboards = new LinkedHashSet<>();
        for (ScoringCompetitor competitor : competitors) {
            List<ScoringHistoryEntry> competitorHistory = historyByCompetitor.getOrDefault(competitor.getId(), List.of());
            competitorDashboards.add(buildCompetitorDashboard(competitor, competitorHistory));
        }

        return ScoringDashboard.builder()
            .competitionId(competition.getId())
            .competitionName(competition.getName())
            .competitors(competitorDashboards)
            .build();
    }

    @Override
    public CompetitionScoringCriteriaResults getCriteriaResultsForCompetition(@NonNull Integer competitionId, boolean viewerIsAdmin) {
        ScoringCompetition competition = assertViewerCanSeeScores(competitionId, viewerIsAdmin);

        List<ScoringCompetitor> competitors = competitorLookup.findByCompetitionId(competitionId);
        List<ScoringCriterion> criteria = competitionCriterionRepository.findCriteriaForCompetition(competitionId);
        List<ScoringHistoryEntry> latestEntries = scoringDashboardQuery.findLatestPerCompetitorAndCriterion(competitionId);

        Map<Integer, Map<Integer, ScoringHistoryEntry>> latestByCompetitorAndCriterion = new HashMap<>();
        for (ScoringHistoryEntry entry : latestEntries) {
            latestByCompetitorAndCriterion
                .computeIfAbsent(entry.getCompetitorId(), id -> new HashMap<>())
                .put(entry.getScoringCriterionId(), entry);
        }

        Set<CompetitorCriteriaResults> competitorResults = new LinkedHashSet<>();
        for (ScoringCompetitor competitor : competitors) {
            Map<Integer, ScoringHistoryEntry> entriesForCompetitor =
                latestByCompetitorAndCriterion.getOrDefault(competitor.getId(), Map.of());
            competitorResults.add(buildCompetitorCriteriaResults(competitor, criteria, entriesForCompetitor));
        }

        return CompetitionScoringCriteriaResults.builder()
            .competitionId(competition.getId())
            .competitionName(competition.getName())
            .competitors(competitorResults)
            .build();
    }

    @Override
    public CompetitorCriteriaResults getCriteriaResultsForCompetitor(@NonNull Integer competitionId, @NonNull Integer competitorId, boolean viewerIsAdmin) {
        assertViewerCanSeeScores(competitionId, viewerIsAdmin);

        ScoringCompetitor competitor = competitorLookup.findById(competitorId)
            .orElseThrow(() -> EntityNotFoundException.competitor(competitorId));

        List<ScoringCriterion> criteria = competitionCriterionRepository.findCriteriaForCompetition(competitionId);
        List<ScoringHistoryEntry> entries = scoringDashboardQuery.findLatestPerCriterionForCompetitor(competitionId, competitorId);

        Map<Integer, ScoringHistoryEntry> entriesByCriterion = new HashMap<>();
        for (ScoringHistoryEntry entry : entries) {
            entriesByCriterion.put(entry.getScoringCriterionId(), entry);
        }

        return buildCompetitorCriteriaResults(competitor, criteria, entriesByCriterion);
    }

    private ScoringCompetition assertViewerCanSeeScores(Integer competitionId, boolean viewerIsAdmin) {
        ScoringCompetition competition = competitionLookup.findById(competitionId)
            .orElseThrow(() -> EntityNotFoundException.competition(competitionId));

        if (!viewerIsAdmin && !competition.isScorePublished()) {
            throw com.arbitaja.refactored.backend.scoring.core.domain.exception.ForbiddenException.scoresNotPublished();
        }
        return competition;
    }

    private CompetitorDashboard buildCompetitorDashboard(ScoringCompetitor competitor, List<ScoringHistoryEntry> history) {
        Map<Integer, Double> latestPointsPerCriterion = new LinkedHashMap<>();
        List<DashboardResultPoint> resultPoints = new ArrayList<>(history.size());

        for (ScoringHistoryEntry entry : history) {
            latestPointsPerCriterion.put(entry.getScoringCriterionId(), entry.getPointsGiven());
            double total = 0.0;
            for (Double value : latestPointsPerCriterion.values()) {
                total += value;
            }
            resultPoints.add(DashboardResultPoint.builder()
                .timestamp(entry.getCreatedAt())
                .pointAmount(total)
                .build());
        }

        Double totalScore = resultPoints.isEmpty() ? 0.0 : resultPoints.getLast().getPointAmount();

        return CompetitorDashboard.builder()
            .competitorId(competitor.getId())
            .competitorName(competitor.resolvedDisplayName())
            .totalScore(totalScore)
            .results(resultPoints)
            .build();
    }

    private CompetitorCriteriaResults buildCompetitorCriteriaResults(
        ScoringCompetitor competitor,
        List<ScoringCriterion> criteria,
        Map<Integer, ScoringHistoryEntry> entriesByCriterion
    ) {
        Set<CriterionResult> results = new LinkedHashSet<>();
        for (ScoringCriterion criterion : criteria) {
            ScoringHistoryEntry entry = entriesByCriterion.get(criterion.getId());
            results.add(CriterionResult.builder()
                .criterionId(criterion.getId())
                .criterionName(criterion.getName())
                .points(entry == null ? 0.0 : entry.getPointsGiven())
                .build());
        }

        return CompetitorCriteriaResults.builder()
            .competitorId(competitor.getId())
            .competitorName(competitor.resolvedDisplayName())
            .criteria(results)
            .build();
    }
}
