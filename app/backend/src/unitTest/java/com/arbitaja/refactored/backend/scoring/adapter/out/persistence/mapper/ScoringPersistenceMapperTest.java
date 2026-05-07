package com.arbitaja.refactored.backend.scoring.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCompetitionJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCompetitorJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCriterionJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringHistoryJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringPersonalDataJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringSchoolJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.projection.ScoringHistoryWithCriterionProjection;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetition;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetitor;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringPersistenceMapperTest {

    private final ScoringPersistenceMapper mapper = new ScoringPersistenceMapper();

    @Test
    void scoringCriterionRoundTripsThroughMapper() {
        ScoringCriterion domain = ScoringCriterion.builder()
            .id(1).name("speed").description("desc")
            .manual(true).totalPoints(10.0).generalized(false)
            .expectedResult("expected").template(false)
            .visibilityLevel(2).scoringHostId(3).criteriaTemplateId(4)
            .build();

        ScoringCriterionJpaEntity entity = mapper.toEntity(domain);
        ScoringCriterion roundTripped = mapper.toDomain(entity);

        assertEquals(domain.getId(), roundTripped.getId());
        assertEquals(domain.getName(), roundTripped.getName());
        assertEquals(domain.getTotalPoints(), roundTripped.getTotalPoints());
        assertTrue(roundTripped.getManual());
    }

    @Test
    void scoringCriterionToEntityWithNullDomainReturnsNull() {
        assertNull(mapper.toEntity((ScoringCriterion) null));
        assertNull(mapper.toDomain((ScoringCriterionJpaEntity) null));
    }

    @Test
    void scoringCompetitionEntityToDomainCopiesFields() {
        Timestamp start = Timestamp.valueOf("2026-04-06 09:00:00");
        Timestamp end = Timestamp.valueOf("2026-04-06 18:00:00");
        ScoringCompetitionJpaEntity entity = ScoringCompetitionJpaEntity.builder()
            .id(1).name("Final").startTime(start).endTime(end).publishScores(true).build();

        ScoringCompetition domain = mapper.toDomain(entity);

        assertEquals(1, domain.getId());
        assertEquals("Final", domain.getName());
        assertTrue(domain.getPublishScores());
    }

    @Test
    void scoringCompetitionToDomainHandlesNull() {
        assertNull(mapper.toDomain((ScoringCompetitionJpaEntity) null));
    }

    @Test
    void scoringCompetitorEntityToDomainResolvesPersonalDataAndSchool() {
        ScoringSchoolJpaEntity school = ScoringSchoolJpaEntity.builder().id(5).name("School").build();
        ScoringPersonalDataJpaEntity personalData = ScoringPersonalDataJpaEntity.builder()
            .id(7).fullName("Alice Full").school(school).build();
        ScoringCompetitorJpaEntity entity = ScoringCompetitorJpaEntity.builder()
            .id(10).alias("alias").publicDisplayNameType(3).personalData(personalData).build();

        ScoringCompetitor domain = mapper.toDomain(entity);

        assertEquals(10, domain.getId());
        assertEquals("alias", domain.getAlias());
        assertEquals("Alice Full", domain.getFullName());
        assertEquals("School", domain.getSchoolName());
    }

    @Test
    void scoringCompetitorEntityToDomainHandlesMissingPersonalData() {
        ScoringCompetitorJpaEntity entity = ScoringCompetitorJpaEntity.builder()
            .id(10).alias("alias").publicDisplayNameType(3).personalData(null).build();

        ScoringCompetitor domain = mapper.toDomain(entity);

        assertNull(domain.getFullName());
        assertNull(domain.getSchoolName());
    }

    @Test
    void scoringHistoryProjectionToDomainCopiesFields() {
        Timestamp now = Timestamp.valueOf("2026-04-06 12:00:00");
        ScoringHistoryWithCriterionProjection projection = new ScoringHistoryWithCriterionProjection() {
            @Override public Integer getId() { return 99; }
            @Override public Integer getCompetitionId() { return 1; }
            @Override public Integer getCompetitorId() { return 2; }
            @Override public Integer getScoringCriterionId() { return 3; }
            @Override public String getScoringCriterionName() { return "ssh"; }
            @Override public Double getPointsGiven() { return 5.0; }
            @Override public Timestamp getCreatedAt() { return now; }
        };

        ScoringHistoryEntry entry = mapper.toDomain(projection);

        assertEquals(99, entry.getId());
        assertEquals("ssh", entry.getScoringCriterionName());
        assertEquals(now, entry.getCreatedAt());
    }

    @Test
    void scoringHistoryEntryRoundTripPreservesFields() {
        Timestamp now = Timestamp.valueOf("2026-04-06 12:00:00");
        ScoringHistoryEntry entry = ScoringHistoryEntry.builder()
            .competitionId(1).competitorId(2).scoringCriterionId(3)
            .pointsGiven(5.0).createdAt(now).scoringCriterionName("ssh").build();

        ScoringHistoryJpaEntity entity = mapper.toEntity(entry);
        ScoringHistoryEntry roundTripped = mapper.toDomain(entity, "ssh");

        assertEquals(1, roundTripped.getCompetitionId());
        assertEquals(2, roundTripped.getCompetitorId());
        assertEquals(3, roundTripped.getScoringCriterionId());
        assertEquals(5.0, roundTripped.getPointsGiven());
        assertEquals("ssh", roundTripped.getScoringCriterionName());
    }
}
