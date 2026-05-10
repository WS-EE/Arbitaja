package com.arbitaja.refactored.backend.competition.core.application.school;

import com.arbitaja.refactored.backend.competition.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.in.school.ManageSchoolUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.school.SchoolRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for school write operations.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ManageSchoolService implements ManageSchoolUseCase {

    private final SchoolRepositoryPort schoolRepository;

    @Override
    @Transactional
    public School createSchool(@NonNull UpsertSchoolCommand command) {
        schoolRepository.findByName(command.getName()).ifPresent(existing -> {
            throw DuplicateEntityException.schoolByName(command.getName());
        });
        return schoolRepository.save(School.builder().name(command.getName()).build());
    }

    @Override
    @Transactional
    public School updateSchool(@NonNull Integer id, @NonNull UpsertSchoolCommand command) {
        schoolRepository.findById(id).orElseThrow(() -> EntityNotFoundException.school(id));

        schoolRepository.findByName(command.getName())
            .filter(existing -> !id.equals(existing.getId()))
            .ifPresent(existing -> {
                throw DuplicateEntityException.schoolByName(command.getName());
            });

        return schoolRepository.save(School.builder().id(id).name(command.getName()).build());
    }

    @Override
    @Transactional
    public void deleteSchool(@NonNull Integer id) {
        schoolRepository.findById(id).orElseThrow(() -> EntityNotFoundException.school(id));
        schoolRepository.deleteById(id);
    }
}

