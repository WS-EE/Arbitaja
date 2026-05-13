package com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorPersonalDataJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorSchoolJpaEntity;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitorPersonalData;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.sql.Timestamp;

/**
 * Maps between competitor persistence entities and the competition-management domain model.
 */
@Component
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitorPersistenceMapper {

    public com.arbitaja.refactored.backend.competition.core.domain.model.Competitor toDomain(CompetitorJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return com.arbitaja.refactored.backend.competition.core.domain.model.Competitor.builder()
            .id(entity.getId())
            .alias(entity.getAlias())
            .publicDisplayNameType(entity.getPublicDisplayNameType())
            .personalData(toDomain(entity.getPersonalData()))
            .build();
    }

    public CompetitorJpaEntity toEntity(com.arbitaja.refactored.backend.competition.core.domain.model.Competitor domain) {
        CompetitorJpaEntity entity = new CompetitorJpaEntity();
        entity.setId(domain.getId());
        entity.setAlias(domain.getAlias());
        entity.setPublicDisplayNameType(domain.getPublicDisplayNameType());
        return entity;
    }

    public CompetitorPersonalData toDomain(CompetitorPersonalDataJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return CompetitorPersonalData.builder()
            .id(entity.getId())
            .fullName(entity.getFullName())
            .email(entity.getEmail())
            .school(toDomain(entity.getSchool()))
            .build();
    }

    public School toDomain(CompetitorSchoolJpaEntity school) {
        if(school == null) {
            return null;
        }
        return School.builder()
            .id(school.getId())
            .name(school.getName())
            .build();
    }

    public CompetitorPersonalDataJpaEntity toNewPersonalDataEntity(CompetitorPersonalData domain, CompetitorSchoolJpaEntity school) {
        CompetitorPersonalDataJpaEntity entity = new CompetitorPersonalDataJpaEntity();
        entity.setFullName(domain.getFullName());
        entity.setEmail(domain.getEmail());
        entity.setSchool(school);
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return entity;
    }
}

