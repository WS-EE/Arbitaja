package com.arbitaja.refactored.backend.competition.core.application.school;

import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.in.school.GetSchoolUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.school.SchoolRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for school read operations.
 */
@Service
@RequiredArgsConstructor
public class GetSchoolService implements GetSchoolUseCase {

    private final SchoolRepositoryPort schoolRepository;

    @Override
    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    @Override
    public School getSchoolById(@NonNull Integer id) {
        return schoolRepository.findById(id)
            .orElseThrow(() -> EntityNotFoundException.school(id));
    }
}

