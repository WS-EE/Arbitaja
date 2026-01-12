package com.arbitaja.refactored.backend.pam.core.port.in.user;

import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.UserProfileResponse;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Input port for user query operations.
 * Defines use cases for retrieving user information.
 */
public interface GetUserUseCase {

    /**
     * Get a user by their ID
     * @param id The user's ID
     * @return Optional containing the user if found
     */
    Optional<User> getUserById(@NonNull Integer id);

    /**
     * Get a user by their username
     * @param username The user's username
     * @return Optional containing the user if found
     */
    Optional<User> getUserByUsername(@NonNull String username);

    /**
     * Get all users in the system
     * @return List of all users
     */
    List<User> getAllUsers();

    /**
     * Get user profile response with roles and permissions
     * @param userId The user's ID
     * @return UserProfileResponse containing full profile data
     */
    UserProfileResponse getUserProfile(@NonNull Integer userId);
}

