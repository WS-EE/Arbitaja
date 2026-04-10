package com.arbitaja.refactored.backend.competition.adapter.util;

import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.request.SchoolUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.response.SchoolResponse;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.in.school.ManageSchoolUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SchoolWebMapperTest {

    @InjectMocks
    private SchoolWebMapper mapper;

    @Test
    void toCommandMapsSchoolNameCorrectly() {
        SchoolUpsertRequest request = new SchoolUpsertRequest("Springfield High");

        ManageSchoolUseCase.UpsertSchoolCommand command = mapper.toCommand(request);

        assertEquals("Springfield High", command.getName());
    }

    @Test
    void toResponseMapsSchoolFieldsCorrectly() {
        School school = School.builder()
            .id(42)
            .name("Test School")
            .build();

        SchoolResponse response = mapper.toResponse(school);

        assertEquals(42, response.id());
        assertEquals("Test School", response.name());
    }

    @Test
    void toCommandAndResponseAreConsistent() {
        String schoolName = "Consistent School";
        SchoolUpsertRequest request = new SchoolUpsertRequest(schoolName);

        ManageSchoolUseCase.UpsertSchoolCommand command = mapper.toCommand(request);

        School school = School.builder()
            .id(1)
            .name(command.getName())
            .build();

        SchoolResponse response = mapper.toResponse(school);

        assertEquals(request.name(), response.name());
    }
}

