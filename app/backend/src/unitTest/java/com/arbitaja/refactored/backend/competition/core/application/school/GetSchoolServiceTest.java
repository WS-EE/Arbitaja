package com.arbitaja.refactored.backend.competition.core.application.school;

import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.out.school.SchoolRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSchoolServiceTest {

    @Mock
    private SchoolRepositoryPort schoolRepository;

    @InjectMocks
    private GetSchoolService service;

    @Test
    void getAllSchoolsReturnsRepositoryResult() {
        List<School> schools = List.of(
            School.builder().id(1).name("A").build(),
            School.builder().id(2).name("B").build()
        );
        when(schoolRepository.findAll()).thenReturn(schools);

        List<School> result = service.getAllSchools();

        assertEquals(2, result.size());
        assertEquals("A", result.getFirst().getName());
    }

    @Test
    void getSchoolByIdThrowsWhenMissing() {
        when(schoolRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getSchoolById(99));
    }
}

