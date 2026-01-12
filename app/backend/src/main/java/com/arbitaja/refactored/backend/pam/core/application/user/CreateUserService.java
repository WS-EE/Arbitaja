package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.SignupResponse;
import com.arbitaja.refactored.backend.pam.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.*;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.personaldata.PersonalDataRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.school.SchoolRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.security.PasswordEncoderPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.SignupUserRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * Application service implementing user creation use cases.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CreateUserService implements CreateUserUseCase {

    private static final String DEFAULT_USER_ROLE = "user";

    private final UserRepositoryPort userRepository;
    private final SignupUserRepositoryPort signupUserRepository;
    private final RoleRepositoryPort roleRepository;
    private final SchoolRepositoryPort schoolRepository;
    private final PersonalDataRepositoryPort personalDataRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    @Transactional
    public SignupUser signupUser(@NonNull SignupCommand command) {
        log.info("Processing signup for username: {}", command.getUsername());

        // Check if username already exists
        checkUsernameTaken(command.getUsername());

        // Get school if provided
        School school = getSchoolIfExists(command.getSchoolId());

        // Create personal data
        PersonalData personalData = PersonalData.builder()
                .fullName(command.getFullName())
                .email(command.getEmail())
                .school(school)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        // Create signup user with encoded password
        String encodedPassword = passwordEncoder.encode(command.getPassword());
        SignupUser signupUser = SignupUser.builder()
                .username(command.getUsername())
                .saltedPassword(encodedPassword)
                .personalData(personalData)
                .build();

        log.info("Creating signup user: {}", command.getUsername());
        signupUserRepository.save(signupUser);
        return signupUser;
    }

    @Override
    @Transactional
    public User approveSignupUser(@NonNull ApproveSignupCommand command) {
        log.info("Approving signup user with id: {}", command.getSignupUserId());

        // Find signup user
        SignupUser signupUser = signupUserRepository.findById(command.getSignupUserId())
                .orElseThrow(() -> EntityNotFoundException.signupUser(command.getSignupUserId()));

        // Apply any updates from command
        if (command.getUsername() != null) {
            signupUser.setUsername(command.getUsername());
        }

        PersonalData personalData = signupUser.getPersonalData();
        if (command.getFullName() != null) {
            personalData.setFullName(command.getFullName());
        }
        if (command.getEmail() != null) {
            personalData.setEmail(command.getEmail());
        }
        if (command.getSchoolId() != null) {
            School school = schoolRepository.findById(command.getSchoolId())
                    .orElseThrow(() -> EntityNotFoundException.school(command.getSchoolId()));
            personalData.setSchool(school);
        }

        // Set created timestamp
        personalData.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Save personal data first
        PersonalData savedPersonalData = personalDataRepository.save(personalData);

        // Create user from signup user
        User newUser = User.createNew(
                signupUser.getUsername(),
                signupUser.getSaltedPassword(),
                savedPersonalData
        );

        User savedUser = userRepository.save(newUser);

        // Assign default role
        Role userRole = roleRepository.findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() -> EntityNotFoundException.roleByName(DEFAULT_USER_ROLE));

        UserRole userRoleAssignment = UserRole.createNew(savedUser, userRole);
        savedUser.addRole(userRoleAssignment);

        // Update user with role
        savedUser = userRepository.save(savedUser);

        // Delete signup user
        signupUserRepository.delete(signupUser);

        log.info("Successfully approved and created user: {}", savedUser.getUsername());
        return savedUser;
    }

    @Override
    @Transactional
    public void declineSignupUser(@NonNull Integer signupUserId) {
        log.info("Declining signup user with id: {}", signupUserId);

        SignupUser signupUser = signupUserRepository.findById(signupUserId)
                .orElseThrow(() -> EntityNotFoundException.signupUser(signupUserId));

        signupUserRepository.delete(signupUser);
        log.info("Successfully declined signup user: {}", signupUserId);
    }

    @Transactional
    @Override
    public User createUser(@NonNull SignupCommand command) {
        log.info("Creating user with username: {}", command.getUsername());

        SignupUser signupUser = signupUser(command);
        log.info("User created with id: {}", signupUser.getId());

        ApproveSignupCommand approveCommand = convertSignupToApproveCommand(command, signupUser.getId());
        User user = approveSignupUser(approveCommand);
        log.info("User approved with id: {}", user.getId());

        return user;
    }

    @Override
    public List<SignupResponse> getAllSignupUsers() {
        List<SignupUser> signupUsers = signupUserRepository.findAll();

        return signupUsers.stream().map(su -> SignupResponse.builder()
                .userId(su.getId())
                .username(su.getUsername())
                .email(su.getPersonalData().getEmail())
                .schoolId(su.getPersonalData().getSchool() != null ? su.getPersonalData().getSchool().getId() : null)
                .message("Signup request retrieved successfully")
                .build()).toList();
    }


    private void checkUsernameTaken(String username) {
        if(userRepository.existsByUsername(username) || signupUserRepository.existsByUsername(username)) {
            throw DuplicateEntityException.userWithUsername(username);
        }
    }

    private School getSchoolIfExists(Integer schoolId) {
        if (schoolId == null) {
            return null;
        }
        return schoolRepository.findById(schoolId)
                .orElseThrow(() -> EntityNotFoundException.school(schoolId));
    }

    private ApproveSignupCommand convertSignupToApproveCommand(SignupCommand command, Integer signupUserId) {
        return ApproveSignupCommand.builder()
                .signupUserId(signupUserId)
                .username(command.getUsername())
                .fullName(command.getFullName())
                .email(command.getEmail())
                .schoolId(command.getSchoolId())
                .build();
    }
}
