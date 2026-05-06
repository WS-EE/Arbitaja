package com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorSchoolJpaEntity;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SchoolPersistenceMapperTest {

    @InjectMocks
    private SchoolPersistenceMapper mapper;

    @Test
    void toDomainConvertsEntityToModel() {
        CompetitorSchoolJpaEntity entity = new CompetitorSchoolJpaEntity();
        entity.setId(1);
        entity.setName("Test School");
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        School domain = mapper.toDomain(entity);

        assertEquals(1, domain.getId());
        assertEquals("Test School", domain.getName());
    }

    @Test
    void toDomainWithNullEntityReturnsNull() {
        School result = mapper.toDomain(null);

        assertNull(result);
    }

    @Test
    void toEntityConvertsModelToEntity() {
        School domain = School.builder()
            .id(42)
            .name("Domain School")
            .build();

        CompetitorSchoolJpaEntity entity = mapper.toEntity(domain);

        assertEquals(42, entity.getId());
        assertEquals("Domain School", entity.getName());
    }

    @Test
    void toEntityAndToDomainAreRoundtrip() {
        CompetitorSchoolJpaEntity original = new CompetitorSchoolJpaEntity();
        original.setId(5);
        original.setName("Original School");
        original.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        School domain = mapper.toDomain(original);
        CompetitorSchoolJpaEntity entity = mapper.toEntity(domain);

        assertEquals(original.getId(), entity.getId());
        assertEquals(original.getName(), entity.getName());
    }

    @Test
    void toDomainHandlesSpecialCharactersInName() {
        CompetitorSchoolJpaEntity entity = new CompetitorSchoolJpaEntity();
        entity.setId(10);
        entity.setName("School-2024 & Academy™");
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        School domain = mapper.toDomain(entity);

        assertEquals("School-2024 & Academy™", domain.getName());
    }
}

