package com.arbitaja.refactored.backend.pam.core.port.out.user;

import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Output port for user persistence operations.
 * This is the interface that the domain uses to interact with persistence.
 */
public interface UserRepositoryPort {

    /**
     * Find user by ID
     * @param id User ID
     * @return Optional containing user if found
     */
    Optional<User> findById(@NonNull Integer id);

    /**
     * Find user by username
     * @param username Username
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(@NonNull String username);

    /**
     * Find all users
     * @return List of all users
     */
    List<User> findAll();

    /**
     * Save a user
     * @param user User to save
     * @return Saved user
     */
    User save(@NonNull User user);

    /**
     * Delete a user
     * @param user User to delete
     */
    void delete(@NonNull User user);

    /**
     * Check if user exists by username
     * @param username Username to check
     * @return true if user exists
     */
    boolean existsByUsername(@NonNull String username);
}

