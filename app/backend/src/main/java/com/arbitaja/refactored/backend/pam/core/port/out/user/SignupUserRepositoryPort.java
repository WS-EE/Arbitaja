package com.arbitaja.refactored.backend.pam.core.port.out.user;

import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Output port for signup user persistence operations.
 */
public interface SignupUserRepositoryPort {

    /**
     * Find signup user by ID
     *
     * @param id SignupUser ID
     * @return Optional containing signup user if found
     */
    Optional<SignupUser> findById(@NonNull Integer id);

    /**
     * Find signup user by username
     *
     * @param username Username
     * @return Optional containing signup user if found
     */
    Optional<SignupUser> findByUsername(@NonNull String username);

    /**
     * Find all signup users
     *
     * @return List of all signup users
     */
    List<SignupUser> findAll();

    /**
     * Save a signup user
     *
     * @param signupUser SignupUser to save
     * @return Saved signup user
     */
    SignupUser save(@NonNull SignupUser signupUser);

    /**
     * Delete a signup user
     *
     * @param signupUser SignupUser to delete
     */
    void delete(@NonNull SignupUser signupUser);

    /**
     * Check if signup user exists by username
     *
     * @param username Username to check
     * @return true if signup user exists
     */
    boolean existsByUsername(@NonNull String username);
}

