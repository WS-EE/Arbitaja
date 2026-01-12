package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.*;
import com.arbitaja.refactored.backend.pam.core.port.in.user.GetUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.UserProfileResponse;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Application service implementing user query use cases.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserService implements GetUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;

    @Override
    public Optional<User> getUserById(@NonNull Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(@NonNull String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserProfileResponse getUserProfile(@NonNull Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> EntityNotFoundException.user(userId));

        List<Role> roles = roleRepository.findByUserId(userId);
        List<Permission> permissions = permissionRepository.findByUserId(userId);

        return mapToUserProfileResponse(user, roles, permissions);
    }

    /**
     * Maps personal data and user to a UserProfileResponse.
     * Used for login responses.
     *
     * @param personalData The personal data to map
     * @param user Optional containing the user
     * @return UserProfileResponse with all user data
     */
    public UserProfileResponse mapPersonalData(PersonalData personalData, Optional<User> user) {
        if (user.isEmpty()) {
            throw EntityNotFoundException.userByUsername("unknown");
        }
        User actualUser = user.get();
        List<Role> roles = roleRepository.findByUserId(actualUser.getId());
        List<Permission> permissions = permissionRepository.findByUserId(actualUser.getId());

        return mapToUserProfileResponse(actualUser, roles, permissions);
    }

    private UserProfileResponse mapToUserProfileResponse(User user, List<Role> roles, List<Permission> permissions) {
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());

        // Convert domain roles to response roles
        List<com.arbitaja.backend.users.dataobjects.Role> responseRoles = roles.stream()
                .map(this::convertToLegacyRole)
                .collect(Collectors.toList());

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRoles(responseRoles);
        response.setPermissions(authorities);

        // Map personal data
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

