package com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.*;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionCompetitor;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionOrganizer;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitorPersonalData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompetitionPersistenceMapperTest {

    @InjectMocks
    private CompetitionPersistenceMapper mapper;

    @Test
    void toDomainConvertsCompetitionEntityCorrectly() {
        CompetitionOrganizerJpaEntity organizerEntity = new CompetitionOrganizerJpaEntity();
        organizerEntity.setId(1);
        organizerEntity.setUsername("admin");

        CompetitorPersonalDataJpaEntity personalData = new CompetitorPersonalDataJpaEntity();
        personalData.setId(10);
        personalData.setFullName("Admin User");
        organizerEntity.setPersonalData(personalData);

        CompetitionJpaEntity entity = new CompetitionJpaEntity();
        entity.setId(5);
        entity.setName("Test Competition");
        entity.setStartTime(new Timestamp(System.currentTimeMillis() + 3600000));
        entity.setEndTime(new Timestamp(System.currentTimeMillis() + 7200000));
        entity.setScoreShowtime(new Timestamp(System.currentTimeMillis() + 5400000));
        entity.setPublishScores(true);
        entity.setOrganizer(organizerEntity);
        entity.setCompetitorCompetitions(new LinkedHashSet<>());

        Competition domain = mapper.toDomain(entity);

        assertEquals(5, domain.getId());
        assertEquals("Test Competition", domain.getName());
        assertTrue(domain.getPublishScores());
        assertNotNull(domain.getOrganizer());
        assertEquals("admin", domain.getOrganizer().getUsername());
        assertEquals("Admin User", domain.getOrganizer().getFullName());
    }

    @Test
    void toDomainWithNullOrganizerReturnsNullOrganizer() {
        CompetitionJpaEntity entity = new CompetitionJpaEntity();
        entity.setId(1);
        entity.setName("No Organizer");
        entity.setStartTime(new Timestamp(System.currentTimeMillis()));
        entity.setEndTime(new Timestamp(System.currentTimeMillis() + 3600000));
        entity.setOrganizer(null);
        entity.setCompetitorCompetitions(new LinkedHashSet<>());

        Competition domain = mapper.toDomain(entity);

        assertNull(domain.getOrganizer());
    }

    @Test
    void toEntityConvertsCompetitionDomainToEntity() {
        Competition domain = Competition.builder()
            .id(3)
            .name("Domain Competition")
            .startTime(new Timestamp(System.currentTimeMillis() + 3600000))
            .endTime(new Timestamp(System.currentTimeMillis() + 7200000))
            .scoreShowtime(new Timestamp(System.currentTimeMillis() + 5400000))
            .publishScores(false)
            .build();

        CompetitionOrganizerJpaEntity organizerEntity = new CompetitionOrganizerJpaEntity();
        organizerEntity.setId(10);

        CompetitionJpaEntity entity = mapper.toEntity(domain, organizerEntity);

        assertEquals(3, entity.getId());
        assertEquals("Domain Competition", entity.getName());
        assertFalse(entity.getPublishScores());
        assertEquals(organizerEntity, entity.getOrganizer());
    }

    @Test
    void toDomainOrganizerConvertsOrganizerEntity() {
        CompetitorPersonalDataJpaEntity personalData = new CompetitorPersonalDataJpaEntity();
        personalData.setFullName("John Organizer");

        CompetitionOrganizerJpaEntity entity = new CompetitionOrganizerJpaEntity();
        entity.setId(20);
        entity.setUsername("john_org");
        entity.setPersonalData(personalData);

        CompetitionOrganizer domain = mapper.toDomain(entity);

        assertEquals(20, domain.getId());
        assertEquals("john_org", domain.getUsername());
        assertEquals("John Organizer", domain.getFullName());
    }

    @Test
    void toDomainOrganizerWithoutPersonalDataReturnsNullFullName() {
        CompetitionOrganizerJpaEntity entity = new CompetitionOrganizerJpaEntity();
        entity.setId(30);
        entity.setUsername("no_data");
        entity.setPersonalData(null);

        CompetitionOrganizer domain = mapper.toDomain(entity);

        assertEquals(30, domain.getId());
        assertEquals("no_data", domain.getUsername());
        assertNull(domain.getFullName());
    }

    @Test
    void toDomainCompetitorsConvertsMultipleEntities() {
        CompetitorJpaEntity competitor1 = new CompetitorJpaEntity();
        competitor1.setId(1);
        competitor1.setAlias("comp1");
        competitor1.setPublicDisplayNameType(1);

        CompetitorJpaEntity competitor2 = new CompetitorJpaEntity();
        competitor2.setId(2);
        competitor2.setAlias("comp2");
        competitor2.setPublicDisplayNameType(2);

        Set<CompetitorJpaEntity> entities = new LinkedHashSet<>();
        entities.add(competitor1);
        entities.add(competitor2);

        Set<CompetitionCompetitor> domain = mapper.toDomainCompetitors(entities);

        assertEquals(2, domain.size());
    }

    @Test
    void toDomainPersonalDataConvertsEntity() {
        CompetitorSchoolJpaEntity schoolEntity = new CompetitorSchoolJpaEntity();
        schoolEntity.setId(5);

        CompetitorPersonalDataJpaEntity entity = new CompetitorPersonalDataJpaEntity();
        entity.setId(50);
        entity.setFullName("Person Name");
        entity.setEmail("person@example.com");
        entity.setSchool(schoolEntity);

        CompetitorPersonalData domain = mapper.toDomain(entity);

        assertEquals(50, domain.getId());
        assertEquals("Person Name", domain.getFullName());
        assertEquals("person@example.com", domain.getEmail());
        assertEquals(5, domain.getSchool().getId());
    }

    @Test
    void toDomainPersonalDataWithNullSchoolReturnsNullSchoolId() {
        CompetitorPersonalDataJpaEntity entity = new CompetitorPersonalDataJpaEntity();
        entity.setId(51);
        entity.setFullName("No School");
        entity.setEmail("no@school.com");
        entity.setSchool(null);

        CompetitorPersonalData domain = mapper.toDomain(entity);

        assertNull(domain.getSchool());
    }
}

