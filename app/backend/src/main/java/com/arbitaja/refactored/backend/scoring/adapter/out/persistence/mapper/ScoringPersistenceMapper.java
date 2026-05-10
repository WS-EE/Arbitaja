package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCompetitionJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCompetitorJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCriterionJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringHistoryJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringCompetitorProjection;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringDashboardRowProjection;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringHistoryWithCriterionProjection;
import com.arbitaja.refactored.backend.scoring.core.domain.model.DashboardResultRow;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetition;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetitor;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Maps between scoring persistence entities and scoring domain models.
 */
@Component
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ScoringPersistenceMapper {

    public ScoringCriterion toDomain(ScoringCriterionJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ScoringCriterion.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .manual(entity.getManual())
            .totalPoints(entity.getTotalPoints())
            .generalized(entity.getGeneralized())
            .expectedResult(entity.getExpectedResult())
            .template(entity.getTemplate())
            .visibilityLevel(entity.getVisibilityLevel())
            .scoringHostId(entity.getScoringHostId())
            .criteriaTemplateId(entity.getCriteriaTemplateId())
            .build();
    }

    public ScoringCriterionJpaEntity toEntity(ScoringCriterion domain) {
        if (domain == null) {
            return null;
        }
        return ScoringCriterionJpaEntity.builder()
            .id(domain.getId())
            .name(domain.getName())
            .description(domain.getDescription())
            .manual(domain.getManual())
            .totalPoints(domain.getTotalPoints())
            .generalized(domain.getGeneralized())
            .expectedResult(domain.getExpectedResult())
            .template(domain.getTemplate())
            .visibilityLevel(domain.getVisibilityLevel())
            .scoringHostId(domain.getScoringHostId())
            .criteriaTemplateId(domain.getCriteriaTemplateId())
            .build();
    }

    public ScoringCompetition toDomain(ScoringCompetitionJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ScoringCompetition.builder()
            .id(entity.getId())
            .name(entity.getName())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .scoreShowtime(entity.getScoreShowtime())
            .publishScores(entity.getPublishScores())
            .build();
    }

    public ScoringCompetitor toDomain(ScoringCompetitorJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        String fullName = entity.getPersonalData() != null ? entity.getPersonalData().getFullName() : null;
        String schoolName = (entity.getPersonalData() != null && entity.getPersonalData().getSchool() != null)
            ? entity.getPersonalData().getSchool().getName()
            : null;
        return ScoringCompetitor.builder()
            .id(entity.getId())
            .alias(entity.getAlias())
            .publicDisplayNameType(entity.getPublicDisplayNameType())
            .fullName(fullName)
            .schoolName(schoolName)
            .build();
    }

    public ScoringCompetitor toDomain(ScoringCompetitorProjection projection) {
        if (projection == null) {
            return null;
        }
        return ScoringCompetitor.builder()
            .id(projection.getId())
            .alias(projection.getAlias())
            .publicDisplayNameType(projection.getPublicDisplayNameType())
            .fullName(projection.getFullName())
            .schoolName(projection.getSchoolName())
            .build();
    }

    public ScoringHistoryJpaEntity toEntity(ScoringHistoryEntry entry) {
        if (entry == null) {
            return null;
        }
        return ScoringHistoryJpaEntity.builder()
            .id(entry.getId())
            .competitionId(entry.getCompetitionId())
            .competitorId(entry.getCompetitorId())
            .scoringCriteriaId(entry.getScoringCriterionId())
            .pointsGiven(entry.getPointsGiven())
            .createdAt(entry.getCreatedAt())
            .build();
    }

    public ScoringHistoryEntry toDomain(ScoringHistoryJpaEntity entity, String criterionName) {
        if (entity == null) {
            return null;
        }
        return ScoringHistoryEntry.builder()
            .id(entity.getId())
            .competitionId(entity.getCompetitionId())
            .competitorId(entity.getCompetitorId())
            .scoringCriterionId(entity.getScoringCriteriaId())
            .scoringCriterionName(criterionName)
            .pointsGiven(entity.getPointsGiven())
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public ScoringHistoryEntry toDomain(ScoringHistoryWithCriterionProjection projection) {
        if (projection == null) {
            return null;
        }
        return ScoringHistoryEntry.builder()
            .id(projection.getId())
            .competitionId(projection.getCompetitionId())
            .competitorId(projection.getCompetitorId())
            .scoringCriterionId(projection.getScoringCriterionId())
            .scoringCriterionName(projection.getScoringCriterionName())
            .pointsGiven(projection.getPointsGiven())
            .createdAt(projection.getCreatedAt())
            .build();
    }

    public DashboardResultRow toDomain(ScoringDashboardRowProjection projection) {
        if (projection == null) {
            return null;
        }
        return DashboardResultRow.builder()
            .competitorId(projection.getCompetitorId())
            .timestamp(projection.getTimestamp())
            .runningTotal(projection.getRunningTotal())
            .build();
    }
}
