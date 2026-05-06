package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.in.user.UpdateUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.personaldata.PersonalDataRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.school.SchoolRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.security.PasswordEncoderPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Application service implementing user update use cases.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UpdateUserService implements UpdateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final SchoolRepositoryPort schoolRepository;
    private final PersonalDataRepositoryPort personalDataRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    @Transactional
    public User updateUserProfile(@NonNull UpdateUserCommand command,
                                  @NonNull String authenticatedUsername,
                                  boolean isAdmin) {
        log.info("Updating user profile for user id: {}", command.getUserId());

        // Find user
        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> EntityNotFoundException.user(command.getUserId()));

        // Authorization check
        if (!isAdmin && !authenticatedUsername.equals(user.getUsername())) {
            throw ForbiddenException.notAuthorizedToModifyUser();
        }

        // Update username
        user.setUsername(command.getUsername());

        // Update or create personal data
        PersonalData personalData = user.getPersonalData();
        if (personalData == null) {
            personalData = new PersonalData();
        }

        personalData.setFullName(command.getFullName());
        personalData.setEmail(command.getEmail());

        // Update school if provided
        if (command.getSchoolId() != null) {
            School school = schoolRepository.findById(command.getSchoolId())
                .orElseThrow(() -> EntityNotFoundException.school(command.getSchoolId()));
            personalData.setSchool(school);
        }

        // Set created_at if not set
        if (personalData.getCreatedAt() == null) {
            personalData.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        // Save personal data
        PersonalData savedPersonalData = personalDataRepository.save(personalData);
        user.setPersonalData(savedPersonalData);

        // Save user
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public boolean changePassword(@NonNull ChangePasswordCommand command, @NonNull String authenticatedUsername) {
        log.info("Changing password for user id: {}", command.getUserId());

        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> EntityNotFoundException.user(command.getUserId()));

        // Verify old password
        if (!command.isAdmin() && !passwordEncoder.matches(command.getOldPassword(), user.getSaltedPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Encode and set new password
        String encodedNewPassword = passwordEncoder.encode(command.getNewPassword());
        user.setSaltedPassword(encodedNewPassword);

        userRepository.save(user);
        log.info("Password changed successfully for user: {}", user.getUsername());

        return true;
    }

    @Override
    @Transactional
    public void deleteUser(@NonNull Integer userId) {
        log.info("Deleting user with id: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> EntityNotFoundException.user(userId));

        userRepository.delete(user);
        log.info("User deleted successfully: {}", userId);
    }
}

