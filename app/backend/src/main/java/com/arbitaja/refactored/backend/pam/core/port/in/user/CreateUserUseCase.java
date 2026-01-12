package com.arbitaja.refactored.backend.pam.core.port.in.user;

import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.SignupResponse;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Input port for user creation operations.
 */
public interface CreateUserUseCase {

    /**
     * Create a signup request for a new user
     * @param command Command containing signup data
     * @return Created SignupUser
     */
    SignupUser signupUser(@NonNull SignupCommand command);

    /**
     * Approve a signup request and create a full user
     * @param command Command containing approval data
     * @return Created User
     */
    User approveSignupUser(@NonNull ApproveSignupCommand command);

    /**
     * Decline/reject a signup request
     * @param signupUserId ID of the signup request to decline
     */
    void declineSignupUser(@NonNull Integer signupUserId);

    List<SignupResponse> getAllSignupUsers();

    @Transactional
    User createUser(@NonNull SignupCommand command);

    /**
     * Command for user signup
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class SignupCommand {
        @NonNull
        private String username;

        @NonNull
        private String password;

        @NonNull
        private String fullName;

        @NonNull
        private String email;

        private Integer schoolId;
    }

    /**
     * Command for approving a signup request
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ApproveSignupCommand {
        @NonNull
        private Integer signupUserId;

        private String username;
        private String fullName;
        private String email;
        private Integer schoolId;
    }
}

