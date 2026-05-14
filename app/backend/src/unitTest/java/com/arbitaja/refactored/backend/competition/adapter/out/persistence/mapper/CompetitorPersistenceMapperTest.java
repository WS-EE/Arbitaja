package com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorPersonalDataJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorSchoolJpaEntity;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitorPersonalData;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompetitorPersistenceMapperTest {

    @InjectMocks
    private CompetitorPersistenceMapper mapper;

    @Test
    void toDomainConvertsEntityToModel() {
        CompetitorPersonalDataJpaEntity personalDataEntity = new CompetitorPersonalDataJpaEntity();
        personalDataEntity.setId(1);
        personalDataEntity.setFullName("Jane Runner");
        personalDataEntity.setEmail("jane@example.com");
        personalDataEntity.setSchool(new CompetitorSchoolJpaEntity(5, "School A", new Timestamp(System.currentTimeMillis())));

        com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity entity =
            new com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity();
        entity.setId(10);
        entity.setAlias("jane_runner");
        entity.setPublicDisplayNameType(2);
        entity.setPersonalData(personalDataEntity);

        Competitor domain = mapper.toDomain(entity);

        assertEquals(10, domain.getId());
        assertEquals("jane_runner", domain.getAlias());
        assertEquals(2, domain.getPublicDisplayNameType());
        assertNotNull(domain.getPersonalData());
        assertEquals(1, domain.getPersonalData().getId());
        assertEquals("Jane Runner", domain.getPersonalData().getFullName());
        assertEquals("jane@example.com", domain.getPersonalData().getEmail());
        assertEquals(5, domain.getPersonalData().getSchool().getId());
    }

    @Test
    void toDomainWithNullEntityReturnsNull() {
        Competitor result = mapper.toDomain((com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity) null);

        assertNull(result);
    }

    @Test
    void toDomainConvertsEntityWithoutPersonalData() {
        com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity entity =
            new com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity();
        entity.setId(20);
        entity.setAlias("no_data");
        entity.setPublicDisplayNameType(1);
        entity.setPersonalData(null);

        Competitor domain = mapper.toDomain(entity);

        assertEquals(20, domain.getId());
        assertEquals("no_data", domain.getAlias());
        assertNull(domain.getPersonalData());
    }

    @Test
    void toEntityConvertsModelToEntity() {
        Competitor domain = Competitor.builder()
            .id(15)
            .alias("test_alias")
            .publicDisplayNameType(3)
            .build();

        com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity entity = mapper.toEntity(domain);

        assertEquals(15, entity.getId());
        assertEquals("test_alias", entity.getAlias());
        assertEquals(3, entity.getPublicDisplayNameType());
    }

    @Test
    void toDomainPersonalDataConvertsEntityToModel() {
        CompetitorSchoolJpaEntity schoolEntity = new CompetitorSchoolJpaEntity(8, "School B", new Timestamp(System.currentTimeMillis()));

        CompetitorPersonalDataJpaEntity entity = new CompetitorPersonalDataJpaEntity();
        entity.setId(2);
        entity.setFullName("John Doe");
        entity.setEmail("john@example.com");
        entity.setSchool(schoolEntity);

        CompetitorPersonalData domain = mapper.toDomain(entity);

        assertEquals(2, domain.getId());
        assertEquals("John Doe", domain.getFullName());
        assertEquals("john@example.com", domain.getEmail());
        assertEquals(8, domain.getSchool().getId());
    }

    @Test
    void toDomainPersonalDataWithNullEntityReturnsNull() {
        CompetitorPersonalData result = mapper.toDomain((CompetitorPersonalDataJpaEntity) null);

        assertNull(result);
    }

    @Test
    void toDomainPersonalDataWithNullSchoolHandledCorrectly() {
        CompetitorPersonalDataJpaEntity entity = new CompetitorPersonalDataJpaEntity();
        entity.setId(3);
        entity.setFullName("No School");
        entity.setEmail("no@school.com");
        entity.setSchool(null);

        CompetitorPersonalData domain = mapper.toDomain(entity);

        assertEquals(3, domain.getId());
        assertEquals("No School", domain.getFullName());
        assertNull(domain.getSchool());
    }

    @Test
    void toNewPersonalDataEntityCreatesEntityWithTimestamp() {
        CompetitorPersonalData domain = CompetitorPersonalData.builder()
            .fullName("New Person")
            .email("new@example.com")
            .school(School.builder()
                .id(6)
                .name("School C")
                .build())
            .build();

        CompetitorSchoolJpaEntity schoolEntity = new CompetitorSchoolJpaEntity(6, "School C", new Timestamp(System.currentTimeMillis()));

        CompetitorPersonalDataJpaEntity entity = mapper.toNewPersonalDataEntity(domain, schoolEntity);

        assertNull(entity.getId());
        assertEquals("New Person", entity.getFullName());
        assertEquals("new@example.com", entity.getEmail());
        assertEquals(schoolEntity, entity.getSchool());
        assertNotNull(entity.getCreatedAt());
    }
}

