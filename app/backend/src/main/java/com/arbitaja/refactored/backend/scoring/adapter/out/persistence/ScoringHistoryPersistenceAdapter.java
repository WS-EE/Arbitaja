package com.arbitaja.refactored.backend.scoring.adapter.out.persistence;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringHistoryJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.mapper.ScoringPersistenceMapper;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringDashboardRowProjection;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository.ScoringHistoryJpaRepository;
import com.arbitaja.refactored.backend.scoring.core.domain.model.DashboardResultRow;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import com.arbitaja.refactored.backend.scoring.core.port.out.history.ScoringDashboardQueryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.history.ScoringHistoryRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence adapter for scoring history. Backs both the write port (saving new entries)
 * and the optimised dashboard read port that drives the dashboard endpoints with three
 * single-query operations rather than the legacy per-competitor / per-criterion loops.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
@Log4j2
public class ScoringHistoryPersistenceAdapter implements ScoringHistoryRepositoryPort, ScoringDashboardQueryPort {

    private final ScoringHistoryJpaRepository scoringHistoryRepository;
    private final ScoringPersistenceMapper mapper;

    @Override
    public ScoringHistoryEntry save(@NonNull ScoringHistoryEntry entry) {
        ScoringHistoryJpaEntity entity = mapper.toEntity(entry);
        ScoringHistoryJpaEntity saved = scoringHistoryRepository.save(entity);
        return mapper.toDomain(saved, entry.getScoringCriterionName());
    }

    @Override
    public List<DashboardResultRow> findHistoryForCompetition(@NonNull Integer competitionId, @NonNull Timestamp cutoff) {
        List<ScoringDashboardRowProjection> row = scoringHistoryRepository.findRunningTotalsForCompetition(competitionId, cutoff);
        List<DashboardResultRow> result = new ArrayList<>(row.size());
        for (ScoringDashboardRowProjection projection : row) {
            result.add(mapper.toDomain(projection));
        }
        return result;
    }

    @Override
    public List<ScoringHistoryEntry> findLatestPerCompetitorAndCriterion(@NonNull Integer competitionId) {
        return scoringHistoryRepository.findLatestPerCompetitorAndCriterion(competitionId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<ScoringHistoryEntry> findLatestPerCriterionForCompetitor(@NonNull Integer competitionId, @NonNull Integer competitorId) {
        return scoringHistoryRepository.findLatestPerCriterionForCompetitor(competitionId, competitorId).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
