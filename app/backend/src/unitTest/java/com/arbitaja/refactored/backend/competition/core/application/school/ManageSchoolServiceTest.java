package com.arbitaja.refactored.backend.competition.core.application.school;

import com.arbitaja.refactored.backend.competition.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.in.school.ManageSchoolUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.school.SchoolRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageSchoolServiceTest {

    @Mock
    private SchoolRepositoryPort schoolRepository;

    @InjectMocks
    private ManageSchoolService service;

    @Test
    void createSchoolStoresNewSchoolWhenNameIsFree() {
        ManageSchoolUseCase.UpsertSchoolCommand command = ManageSchoolUseCase.UpsertSchoolCommand.builder()
            .name("Alpha")
            .build();

        when(schoolRepository.findByName("Alpha")).thenReturn(Optional.empty());
        when(schoolRepository.save(any(School.class))).thenReturn(School.builder().id(1).name("Alpha").build());

        School result = service.createSchool(command);

        assertEquals(1, result.getId());
        assertEquals("Alpha", result.getName());
    }

    @Test
    void createSchoolThrowsWhenNameAlreadyExists() {
        ManageSchoolUseCase.UpsertSchoolCommand command = ManageSchoolUseCase.UpsertSchoolCommand.builder()
            .name("Alpha")
            .build();

        when(schoolRepository.findByName("Alpha")).thenReturn(Optional.of(School.builder().id(9).name("Alpha").build()));

        assertThrows(DuplicateEntityException.class, () -> service.createSchool(command));
    }

    @Test
    void updateSchoolThrowsWhenSchoolDoesNotExist() {
        ManageSchoolUseCase.UpsertSchoolCommand command = ManageSchoolUseCase.UpsertSchoolCommand.builder()
            .name("New Name")
            .build();

        when(schoolRepository.findById(44)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateSchool(44, command));
    }

    @Test
    void updateSchoolThrowsWhenNameBelongsToAnotherSchool() {
        ManageSchoolUseCase.UpsertSchoolCommand command = ManageSchoolUseCase.UpsertSchoolCommand.builder()
            .name("Taken")
            .build();

        when(schoolRepository.findById(3)).thenReturn(Optional.of(School.builder().id(3).name("Old").build()));
        when(schoolRepository.findByName("Taken")).thenReturn(Optional.of(School.builder().id(8).name("Taken").build()));

        assertThrows(DuplicateEntityException.class, () -> service.updateSchool(3, command));
    }

    @Test
    void deleteSchoolDeletesExistingSchool() {
        when(schoolRepository.findById(5)).thenReturn(Optional.of(School.builder().id(5).name("Delete Me").build()));

        service.deleteSchool(5);

        verify(schoolRepository).deleteById(5);
    }
}

