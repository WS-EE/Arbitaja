package com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorSchoolJpaEntity;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Maps between school persistence entities and school domain model.
 */
@Component
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class SchoolPersistenceMapper {

    public School toDomain(CompetitorSchoolJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return School.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }

    public CompetitorSchoolJpaEntity toEntity(School domain) {
        CompetitorSchoolJpaEntity entity = new CompetitorSchoolJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        return entity;
    }
}

