package com.arbitaja.refactored.backend.competition.adapter.in.web.school;

import com.arbitaja.refactored.backend.common.PagedResponse;
import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.request.SchoolUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.response.SchoolResponse;
import com.arbitaja.refactored.backend.competition.adapter.util.SchoolWebMapper;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.in.school.GetSchoolUseCase;
import com.arbitaja.refactored.backend.competition.core.port.in.school.ManageSchoolUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchoolControllerV2Test {

    @Mock
    private GetSchoolUseCase getSchoolUseCase;

    @Mock
    private ManageSchoolUseCase manageSchoolUseCase;

    private SchoolControllerV2 controller;

    @BeforeEach
    void setUp() {
        controller = new SchoolControllerV2(getSchoolUseCase, manageSchoolUseCase, new SchoolWebMapper());
    }

    @Test
    void getAllSchoolsReturnsMappedResponseList() {
        School first = School.builder().id(1).name("School A").build();
        School second = School.builder().id(2).name("School B").build();
        Pageable pageable = PageRequest.of(0, 20);

        when(getSchoolUseCase.getSchoolsPaged(eq(""), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(first, second), pageable, 2));

        ResponseEntity<PagedResponse<SchoolResponse>> response = controller.getAllSchools("", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().content().size());
        assertEquals("School A", response.getBody().content().get(0).name());
        assertEquals("School B", response.getBody().content().get(1).name());
    }

    @Test
    void getSchoolByIdReturnsMappedResponse() {
        School school = School.builder().id(5).name("Delta").build();

        when(getSchoolUseCase.getSchoolById(5)).thenReturn(school);
        ResponseEntity<SchoolResponse> response = controller.getSchoolById(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().id());
        assertEquals("Delta", response.getBody().name());
    }

    @Test
    void createSchoolReturnsCreatedResponse() {
        SchoolUpsertRequest request = new SchoolUpsertRequest("Nova");
        School created = School.builder().id(11).name("Nova").build();

        when(manageSchoolUseCase.createSchool(any())).thenReturn(created);

        ResponseEntity<SchoolResponse> response = controller.createSchool(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(11, response.getBody().id());
        assertEquals("Nova", response.getBody().name());
    }

    @Test
    void updateSchoolReturnsUpdatedResponse() {
        SchoolUpsertRequest request = new SchoolUpsertRequest("Updated");
        School updated = School.builder().id(7).name("Updated").build();

        when(manageSchoolUseCase.updateSchool(eq(7), any())).thenReturn(updated);

        ResponseEntity<SchoolResponse> response = controller.updateSchool(7, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(7, response.getBody().id());
        assertEquals("Updated", response.getBody().name());
    }

    @Test
    void deleteSchoolReturnsSuccessMessage() {
        ResponseEntity<com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.response.GeneralMessageResponse> response =
            controller.deleteSchool(3);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("School deleted successfully", response.getBody().message());
        verify(manageSchoolUseCase).deleteSchool(3);
    }
}
