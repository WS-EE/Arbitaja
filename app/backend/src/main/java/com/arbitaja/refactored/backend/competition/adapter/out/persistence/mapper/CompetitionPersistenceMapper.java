package com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionOrganizerJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorCompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorPersonalDataJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorSchoolJpaEntity;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionCompetitor;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionOrganizer;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitorPersonalData;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Maps between competition persistence entities and competition management domain models.
 */
@Component
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitionPersistenceMapper {

    public Competition toDomain(CompetitionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Competition.builder()
            .id(entity.getId())
            .name(entity.getName())
            .startTime(entity.getStartTime())
            .endTime(entity.getEndTime())
            .scoreShowtime(entity.getScoreShowtime())
            .publishScores(entity.getPublishScores())
            .organizer(toDomain(entity.getOrganizer()))
            .competitors(toDomainCompetitors(entity.getCompetitorCompetitions().stream()
                .map(CompetitorCompetitionJpaEntity::getCompetitor)
                .collect(Collectors.toSet())))
            .build();
    }

    public Competition toDomain(
        CompetitionJpaEntity entity,
        Set<CompetitionCompetitor> competitors
    ) {
        Competition domain = toDomain(entity);
        domain.setCompetitors(competitors);
        return domain;
    }

    public CompetitionJpaEntity toEntity(
        Competition domain,
        CompetitionOrganizerJpaEntity organizer
    ) {
        CompetitionJpaEntity competition = new CompetitionJpaEntity();
        if (domain.getId() != null) {
            competition.setId(domain.getId());
        }
        competition.setName(domain.getName());
        competition.setStartTime(domain.getStartTime());
        competition.setEndTime(domain.getEndTime());
        competition.setScoreShowtime(domain.getScoreShowtime());
        competition.setPublishScores(domain.getPublishScores());
        competition.setOrganizer(organizer);
        return competition;
    }

    public CompetitionOrganizer toDomain(CompetitionOrganizerJpaEntity user) {
        if (user == null) {
            return null;
        }
        String fullName = user.getPersonalData() != null ? user.getPersonalData().getFullName() : null;
        return CompetitionOrganizer.builder()
            .id(user.getId())
            .fullName(fullName)
            .username(user.getUsername())
            .build();
    }

    public Set<CompetitionCompetitor> toDomainCompetitors(Set<CompetitorJpaEntity> competitors) {
        return competitors.stream()
            .map(this::toDomain)
            .collect(Collectors.toSet());
    }

    public CompetitorPersonalData toDomain(CompetitorPersonalDataJpaEntity personalData) {
        if (personalData == null) {
            return null;
        }
        return CompetitorPersonalData.builder()
            .id(personalData.getId())
            .fullName(personalData.getFullName())
            .email(personalData.getEmail())
            .school(toDomain(personalData.getSchool()))
            .build();
    }

    private School toDomain(CompetitorSchoolJpaEntity school) {
        if (school == null) {
            return null;
        }
        return School.builder()
            .id(school.getId())
            .name(school.getName())
            .build();
    }

    public CompetitionCompetitor toDomain(CompetitorJpaEntity competitor) {
        String fullName = competitor.getPersonalData() != null ? competitor.getPersonalData().getFullName() : null;
        return CompetitionCompetitor.builder()
            .id(competitor.getId())
            .fullName(fullName)
            .alias(competitor.getAlias())
            .publicDisplayNameType(competitor.getPublicDisplayNameType())
            .personalData(competitor.getPersonalData() != null ? toDomain(competitor.getPersonalData()) : null)
            .build();
    }
}

