package com.arbitaja.refactored.backend.competition.adapter.util;

import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.request.CompetitionUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.response.CompetitionResponse;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionCompetitor;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionOrganizer;
import com.arbitaja.refactored.backend.competition.core.port.in.competition.ManageCompetitionUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompetitionWebMapperTest {

    @InjectMocks
    private CompetitionWebMapper mapper;

    @Test
    void toCommandMapsRequestFieldsCorrectly() {
        Timestamp startTime = new Timestamp(System.currentTimeMillis() + 3600000);
        Timestamp endTime = new Timestamp(System.currentTimeMillis() + 7200000);
        Timestamp scoreShowtime = new Timestamp(System.currentTimeMillis() + 5400000);

        CompetitionUpsertRequest request = new CompetitionUpsertRequest(
            "Test Competition",
            startTime,
            endTime,
            scoreShowtime,
            true,
            42
        );

        ManageCompetitionUseCase.UpsertCompetitionCommand command = mapper.toCommand(request);

        assertEquals("Test Competition", command.getName());
        assertEquals(startTime, command.getStartTime());
        assertEquals(endTime, command.getEndTime());
        assertEquals(scoreShowtime, command.getScoreShowtime());
        assertTrue(command.getPublishScores());
        assertEquals(42, command.getOrganizerId());
    }

    @Test
    void toResponseMapsCompetitionWithNullOrganizer() {
        Competition competition = Competition.builder()
            .id(1)
            .name("No Organizer Competition")
            .startTime(new Timestamp(System.currentTimeMillis()))
            .endTime(new Timestamp(System.currentTimeMillis() + 3600000))
            .publishScores(false)
            .organizer(null)
            .competitors(new LinkedHashSet<>())
            .build();

        CompetitionResponse response = mapper.toResponse(competition);

        assertEquals(1, response.id());
        assertEquals("No Organizer Competition", response.name());
        assertNull(response.organizer());
        assertNotNull(response.competitors());
        assertTrue(response.competitors().isEmpty());
    }

    @Test
    void toResponseMapsCompetitionWithOrganizer() {
        CompetitionOrganizer organizer = CompetitionOrganizer.builder()
            .id(100)
            .fullName("John Organizer")
            .username("john_org")
            .build();

        Competition competition = Competition.builder()
            .id(5)
            .name("Organized Competition")
            .startTime(new Timestamp(System.currentTimeMillis()))
            .endTime(new Timestamp(System.currentTimeMillis() + 3600000))
            .publishScores(true)
            .organizer(organizer)
            .competitors(new LinkedHashSet<>())
            .build();

        CompetitionResponse response = mapper.toResponse(competition);

        assertEquals(5, response.id());
        assertEquals("Organized Competition", response.name());
        assertNotNull(response.organizer());
        assertEquals(100, response.organizer().id());
        assertEquals("John Organizer", response.organizer().fullName());
        assertEquals("john_org", response.organizer().username());
    }

    @Test
    void toResponseMapsCompetitionWithCompetitors() {
        CompetitionCompetitor competitor = CompetitionCompetitor.builder()
            .id(10)
            .fullName("Competitor Name")
            .alias("competitor_alias")
            .build();

        Set<CompetitionCompetitor> competitors = new LinkedHashSet<>();
        competitors.add(competitor);

        Competition competition = Competition.builder()
            .id(7)
            .name("Competition With Competitors")
            .startTime(new Timestamp(System.currentTimeMillis()))
            .endTime(new Timestamp(System.currentTimeMillis() + 3600000))
            .publishScores(false)
            .organizer(null)
            .competitors(competitors)
            .build();

        CompetitionResponse response = mapper.toResponse(competition);

        assertNotNull(response.competitors());
        assertEquals(1, response.competitors().size());
        CompetitionResponse.CompetitorResponse compResponse = response.competitors().iterator().next();
        assertEquals(10, compResponse.id());
        assertEquals("Competitor Name", compResponse.fullName());
        assertEquals("competitor_alias", compResponse.alias());
    }

    @Test
    void toResponseMapsNullScoreShowtimeCorrectly() {
        Competition competition = Competition.builder()
            .id(1)
            .name("Test")
            .startTime(new Timestamp(System.currentTimeMillis()))
            .endTime(new Timestamp(System.currentTimeMillis() + 3600000))
            .scoreShowtime(null)
            .publishScores(false)
            .competitors(new LinkedHashSet<>())
            .build();

        CompetitionResponse response = mapper.toResponse(competition);

        assertNull(response.scoreShowtime());
    }
}

