package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.domain.model.UserRole;
import com.arbitaja.refactored.backend.pam.core.port.in.user.ManageUserRolesUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.UserRoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service implementing user-role management use cases.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ManageUserRolesService implements ManageUserRolesUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final UserRoleRepositoryPort userRoleRepositoryPort;

    @Override
    public User overwriteUserRoles(Integer userId, List<Integer> roleIds) {
        log.info("Overwriting roles {} for user {}", roleIds, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<UserRole> rolesToRemove = user.getUserRoles().stream()
            .filter(userRole -> !roleIds.contains(userRole.getRole().getId()))
            .toList();

        rolesToRemove.forEach(userRoleRepositoryPort::deleteUserRole);
        rolesToRemove.forEach(user.getUserRoles()::remove);

        roleIds.forEach(roleId -> {
                    Role role = roleRepository.findById(roleId)
                            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
                    if(role.getUserRoles().stream().anyMatch(ur -> ur.getUser().getId().equals(userId))) {
                        return;
                    }
                    UserRole userRole = UserRole.createNew(user, role);
                    userRole = userRoleRepositoryPort.saveUserRole(userRole);
                    user.addRole(userRole);
                });

        return userRepository.save(user);
    }
}

