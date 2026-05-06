package com.arbitaja.refactored.backend.competition.core.port.in.school;

import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import lombok.NonNull;

import java.util.List;

/**
 * Input port for school read operations.
 */
public interface GetSchoolUseCase {

    List<School> getAllSchools();

    School getSchoolById(@NonNull Integer id);
}

