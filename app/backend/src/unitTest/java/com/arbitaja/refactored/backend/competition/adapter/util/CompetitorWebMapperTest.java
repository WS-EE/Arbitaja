package com.arbitaja.refactored.backend.competition.adapter.util;

import com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.request.CompetitorUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.response.CompetitorResponse;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitorPersonalData;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.in.competitor.ManageCompetitorUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompetitorWebMapperTest {

    @InjectMocks
    private CompetitorWebMapper mapper;

    @Test
    void toCommandMapsRequestFieldsCorrectly() {
        CompetitorUpsertRequest request = new CompetitorUpsertRequest(
            "runner_alias",
            2,
            null,
            "Runner Name",
            "runner@example.com",
            5,
            1
        );

        ManageCompetitorUseCase.UpsertCompetitorCommand command = mapper.toCommand(request);

        assertEquals("runner_alias", command.getAlias());
        assertEquals(2, command.getPublicDisplayNameType());
        assertNull(command.getPersonalDataId());
        assertEquals("Runner Name", command.getFullName());
        assertEquals("runner@example.com", command.getEmail());
        assertEquals(5, command.getSchoolId());
        assertEquals(1, command.getCompetitionId());
    }

    @Test
    void toResponseMapsCompetitorWithoutPersonalData() {
        Competitor competitor = Competitor.builder()
            .id(1)
            .alias("test_alias")
            .publicDisplayNameType(1)
            .personalData(null)
            .build();

        CompetitorResponse response = mapper.toResponse(competitor);

        assertEquals(1, response.id());
        assertEquals("test_alias", response.alias());
        assertEquals(1, response.publicDisplayNameType());
        assertNull(response.personalData());
    }

    @Test
    void toResponseMapsCompetitorWithPersonalData() {
        CompetitorPersonalData personalData = CompetitorPersonalData.builder()
            .id(10)
            .fullName("John Competitor")
            .email("john@example.com")
            .school( School.builder()
                .id(3)
                .name("Test School")
                    .build())
            .build();

        Competitor competitor = Competitor.builder()
            .id(5)
            .alias("john_alias")
            .publicDisplayNameType(2)
            .personalData(personalData)
            .build();

        CompetitorResponse response = mapper.toResponse(competitor);

        assertEquals(5, response.id());
        assertEquals("john_alias", response.alias());
        assertEquals(2, response.publicDisplayNameType());
        assertNotNull(response.personalData());
        assertEquals(10, response.personalData().id());
        assertEquals("John Competitor", response.personalData().fullName());
        assertEquals("john@example.com", response.personalData().email());
        assertEquals(3, response.personalData().school().id());
        assertEquals("Test School", response.personalData().school().name());
    }

    @Test
    void toCommandMapsAllNullableFields() {
        CompetitorUpsertRequest request = new CompetitorUpsertRequest(
            "minimal",
            1,
            null,
            null,
            null,
            null,
            null
        );

        ManageCompetitorUseCase.UpsertCompetitorCommand command = mapper.toCommand(request);

        assertEquals("minimal", command.getAlias());
        assertEquals(1, command.getPublicDisplayNameType());
        assertNull(command.getPersonalDataId());
        assertNull(command.getFullName());
        assertNull(command.getEmail());
        assertNull(command.getSchoolId());
        assertNull(command.getCompetitionId());
    }
}

