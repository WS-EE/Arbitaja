package com.arbitaja.refactored.backend.scoring.adapter.out.persistence;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringHistoryJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.mapper.ScoringPersistenceMapper;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringDashboardRowProjection;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringHistoryWithCriterionProjection;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository.ScoringHistoryJpaRepository;
import com.arbitaja.refactored.backend.scoring.core.domain.model.DashboardResultRow;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringHistoryPersistenceAdapterTest {

    @Mock
    private ScoringHistoryJpaRepository scoringHistoryRepository;

    @Mock
    private ScoringPersistenceMapper mapper;

    @InjectMocks
    private ScoringHistoryPersistenceAdapter adapter;

    private ScoringHistoryWithCriterionProjection projection(int id, int competitorId, double points) {
        Timestamp now = Timestamp.valueOf("2026-04-06 12:00:00");
        return new ScoringHistoryWithCriterionProjection() {
            @Override public Integer getId() { return id; }
            @Override public Integer getCompetitionId() { return 1; }
            @Override public Integer getCompetitorId() { return competitorId; }
            @Override public Integer getScoringCriterionId() { return 3; }
            @Override public String getScoringCriterionName() { return "ssh"; }
            @Override public Double getPointsGiven() { return points; }
            @Override public Timestamp getCreatedAt() { return now; }
        };
    }

    @Test
    void saveDelegatesThroughMapperAndPreservesCriterionName() {
        ScoringHistoryEntry entry = ScoringHistoryEntry.builder()
            .competitionId(1).competitorId(2).scoringCriterionId(3)
            .pointsGiven(5.0).scoringCriterionName("ssh").build();
        ScoringHistoryJpaEntity entity = ScoringHistoryJpaEntity.builder().build();
        ScoringHistoryJpaEntity persisted = ScoringHistoryJpaEntity.builder().id(99).build();
        ScoringHistoryEntry returned = ScoringHistoryEntry.builder().id(99).scoringCriterionName("ssh").build();

        when(mapper.toEntity(entry)).thenReturn(entity);
        when(scoringHistoryRepository.save(entity)).thenReturn(persisted);
        when(mapper.toDomain(persisted, "ssh")).thenReturn(returned);

        ScoringHistoryEntry result = adapter.save(entry);

        assertEquals(99, result.getId());
        verify(scoringHistoryRepository).save(entity);
    }

    @Test
    void findHistoryForCompetitionMapsRunningTotalRows() {
        Timestamp cutoff = Timestamp.valueOf("2026-04-06 18:00:00");
        Timestamp t1 = Timestamp.valueOf("2026-04-06 10:00:00");
        Timestamp t2 = Timestamp.valueOf("2026-04-06 11:00:00");

        ScoringDashboardRowProjection row1 = dashboardRow(10, t1, 3.0);
        ScoringDashboardRowProjection row2 = dashboardRow(20, t2, 4.0);
        when(scoringHistoryRepository.findRunningTotalsForCompetition(1, cutoff))
            .thenReturn(List.of(row1, row2));
        when(mapper.toDomain(row1)).thenReturn(
            DashboardResultRow.builder().competitorId(10).timestamp(t1).runningTotal(3.0).build());
        when(mapper.toDomain(row2)).thenReturn(
            DashboardResultRow.builder().competitorId(20).timestamp(t2).runningTotal(4.0).build());

        List<DashboardResultRow> result = adapter.findHistoryForCompetition(1, cutoff);

        assertEquals(2, result.size());
        assertEquals(3.0, result.get(0).getRunningTotal());
        assertEquals(20, result.get(1).getCompetitorId());
        verify(scoringHistoryRepository).findRunningTotalsForCompetition(1, cutoff);
    }

    private ScoringDashboardRowProjection dashboardRow(int competitorId, Timestamp timestamp, double runningTotal) {
        return new ScoringDashboardRowProjection() {
            @Override public Integer getCompetitorId() { return competitorId; }
            @Override public Timestamp getTimestamp() { return timestamp; }
            @Override public Double getRunningTotal() { return runningTotal; }
        };
    }

    @Test
    void findLatestPerCompetitorAndCriterionDelegatesToRepository() {
        ScoringHistoryWithCriterionProjection row = projection(1, 10, 5.0);
        when(scoringHistoryRepository.findLatestPerCompetitorAndCriterion(1)).thenReturn(List.of(row));
        when(mapper.toDomain(row)).thenReturn(ScoringHistoryEntry.builder().id(1).build());

        List<ScoringHistoryEntry> result = adapter.findLatestPerCompetitorAndCriterion(1);

        assertEquals(1, result.size());
        verify(scoringHistoryRepository).findLatestPerCompetitorAndCriterion(1);
    }

    @Test
    void findLatestPerCriterionForCompetitorDelegatesToRepository() {
        when(scoringHistoryRepository.findLatestPerCriterionForCompetitor(1, 10)).thenReturn(List.of());

        List<ScoringHistoryEntry> result = adapter.findLatestPerCriterionForCompetitor(1, 10);

        assertEquals(0, result.size());
        verify(scoringHistoryRepository).findLatestPerCriterionForCompetitor(1, 10);
    }
}
