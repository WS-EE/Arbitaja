package com.arbitaja.refactored.backend.pam.core.port.in.user;

import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import lombok.*;

/**
 * Input port for user update operations.
 */
public interface UpdateUserUseCase {

    /**
     * Update user profile data
     *
     * @param command               Command containing update data
     * @param authenticatedUsername Username of the authenticated user performing the update
     * @param isAdmin               Whether the authenticated user is an admin
     * @return Updated UserProfileResponse
     */
    User updateUserProfile(@NonNull UpdateUserCommand command, @NonNull String authenticatedUsername, boolean isAdmin);

    /**
     * Change user password
     *
     * @param command               Command containing password change data
     * @param authenticatedUsername Username of the authenticated user
     * @return true if password was changed successfully
     */
    boolean changePassword(@NonNull ChangePasswordCommand command, @NonNull String authenticatedUsername);

    /**
     * Delete a user
     *
     * @param userId ID of the user to delete
     */
    void deleteUser(@NonNull Integer userId);

    /**
     * Command for updating user profile
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UpdateUserCommand {
        @NonNull
        private Integer userId;

        @NonNull
        private String username;

        @NonNull
        private String fullName;

        @NonNull
        private String email;

        private Integer schoolId;
    }

    /**
     * Command for changing password
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ChangePasswordCommand {
        @NonNull
        private Integer userId;

        @NonNull
        private String oldPassword;

        @NonNull
        private String newPassword;
    }
}

