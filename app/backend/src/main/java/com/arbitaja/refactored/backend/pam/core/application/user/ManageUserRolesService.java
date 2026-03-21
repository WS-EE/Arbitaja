package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.domain.model.UserRole;
import com.arbitaja.refactored.backend.pam.core.port.in.user.ManageUserRolesUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Application service implementing user-role management use cases.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ManageUserRolesService implements ManageUserRolesUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;

    @Override
    public User overwriteUserRoles(Integer userId, List<Integer> roleIds) {
        log.info("Overwriting roles {} for user {}", roleIds, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Set<Integer> requestedRoleIds = roleIds == null
                ? Set.of()
                : new LinkedHashSet<>(roleIds);

        user.setUserRoles(
                requestedRoleIds.stream()
                        .map(roleId -> {
                            Role role = roleRepository.findById(roleId)
                                    .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
                            return UserRole.createNew(user, role);
                        })
                        .collect(Collectors.toSet())
        );

        return userRepository.save(user);
    }
}

