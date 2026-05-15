package com.arbitaja.refactored.backend.competition.core.port.out.school;

import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Output port for school persistence operations.
 */
public interface SchoolRepositoryPort {

    List<School> findAll();

    Optional<School> findById(@NonNull Integer id);

    Optional<School> findByName(@NonNull String name);

    School save(@NonNull School school);

    void deleteById(@NonNull Integer id);

    Page<School> findPaged(String search, Pageable pageable);
}

