package com.arbitaja.refactored.backend.pam.core.port.out.school;

import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Output port for school persistence operations.
 */
public interface SchoolRepositoryPort {

    /**
     * Find school by ID
     *
     * @param id School ID
     * @return Optional containing school if found
     */
    Optional<School> findById(@NonNull Integer id);

    /**
     * Find all schools
     *
     * @return List of all schools
     */
    List<School> findAll();

    /**
     * Save a school
     *
     * @param school School to save
     * @return Saved school
     */
    School save(@NonNull School school);

    /**
     * Delete a school
     *
     * @param school School to delete
     */
    void delete(@NonNull School school);
}

