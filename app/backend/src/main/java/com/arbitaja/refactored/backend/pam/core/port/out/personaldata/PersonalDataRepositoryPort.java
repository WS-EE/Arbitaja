package com.arbitaja.refactored.backend.pam.core.port.out.personaldata;

import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import lombok.NonNull;

import java.util.Optional;

/**
 * Output port for personal data persistence operations.
 */
public interface PersonalDataRepositoryPort {

    /**
     * Find personal data by ID
     *
     * @param id PersonalData ID
     * @return Optional containing personal data if found
     */
    Optional<PersonalData> findById(@NonNull Integer id);

    /**
     * Save personal data
     *
     * @param personalData PersonalData to save
     * @return Saved personal data
     */
    PersonalData save(@NonNull PersonalData personalData);

    /**
     * Delete personal data
     *
     * @param personalData PersonalData to delete
     */
    void delete(@NonNull PersonalData personalData);
}

