package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.domain.model.*;
import com.arbitaja.refactored.backend.pam.core.port.in.user.UpdateUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.personaldata.PersonalDataRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.school.SchoolRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.security.PasswordEncoderPort;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.UserProfileResponse;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(@NonNull UpdateUserCommand command,
                                                  @NonNull String authenticatedUsername,
                                                  boolean isAdmin) {
        log.info("Updating user profile for user id: {}", command.getUserId());

        // Find user
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> EntityNotFoundException.user(command.getUserId()));

        // Authorization check
        if (!isAdmin && !authenticatedUsername.equals(user.getUsername())) {
            throw UnauthorizedException.notAuthorizedToModifyUser();
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
        User savedUser = userRepository.save(user);

        // Get roles and permissions for response
        List<Role> roles = roleRepository.findByUserId(savedUser.getId());
        List<Permission> permissions = permissionRepository.findByUserId(savedUser.getId());

        return mapToUserProfileResponse(savedUser, roles, permissions);
    }

    @Override
    @Transactional
    public boolean changePassword(@NonNull ChangePasswordCommand command, @NonNull String authenticatedUsername) {
        log.info("Changing password for user id: {}", command.getUserId());

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> EntityNotFoundException.user(command.getUserId()));

        // Authorization check - only the user themselves can change their password
        if (!authenticatedUsername.equals(user.getUsername())) {
            throw UnauthorizedException.notAuthorizedToModifyUser();
        }

        // Verify old password
        if (!passwordEncoder.matches(command.getOldPassword(), user.getSaltedPassword())) {
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

    private UserProfileResponse mapToUserProfileResponse(User user, List<Role> roles, List<Permission> permissions) {
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());

        List<com.arbitaja.backend.users.dataobjects.Role> responseRoles = roles.stream()
                .map(this::convertToLegacyRole)
                .collect(Collectors.toList());

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRoles(responseRoles);
        response.setPermissions(authorities);

        if (user.getPersonalData() != null) {
            UserProfileResponse.PersonalDataResponse personalDataResponse = new UserProfileResponse.PersonalDataResponse();
            personalDataResponse.setId(user.getPersonalData().getId());
            personalDataResponse.setFullName(user.getPersonalData().getFullName());
            personalDataResponse.setEmail(user.getPersonalData().getEmail());

            if (user.getPersonalData().getSchool() != null) {
                UserProfileResponse.SchoolResponse schoolResponse = new UserProfileResponse.SchoolResponse();
                schoolResponse.setId(user.getPersonalData().getSchool().getId());
                schoolResponse.setName(user.getPersonalData().getSchool().getName());
                personalDataResponse.setSchool(schoolResponse);
            }

            response.setPersonalData(personalDataResponse);
        }

        return response;
    }

    private com.arbitaja.backend.users.dataobjects.Role convertToLegacyRole(Role domainRole) {
        com.arbitaja.backend.users.dataobjects.Role legacyRole = new com.arbitaja.backend.users.dataobjects.Role();
        legacyRole.setId(domainRole.getId());
        legacyRole.setName(domainRole.getName());
        legacyRole.setCreated_at(domainRole.getCreatedAt());
        legacyRole.setChanged_at(domainRole.getChangedAt());
        return legacyRole;
    }
}

