package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.domain.model.UserRole;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.UserRoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageUserRolesServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    @Mock
    UserRoleRepositoryPort userRoleRepositoryPort;

    @InjectMocks
    private ManageUserRolesService manageUserRolesService;

    @Test
    void overwriteUserRolesReplacesRolesAndPersists() {
        User user = User.builder().id(5).username("alice").saltedPassword("hash").build();
        Role role1 = Role.builder().id(1).name("user").build();

        when(userRepository.findById(5)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1)).thenReturn(Optional.of(role1));
        when(userRepository.save(user)).thenReturn(user);

        User result = manageUserRolesService.overwriteUserRoles(5, List.of(1));

        verify(userRoleRepositoryPort).saveUserRole(any(UserRole.class));
        assertEquals(user, result);
    }

    @Test
    void overwriteUserRolesThrowsWhenUserMissing() {
        when(userRepository.findById(40)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> manageUserRolesService.overwriteUserRoles(40, List.of(1)));
    }

    @Test
    void overwriteUserRolesThrowsWhenRoleMissing() {
        User user = User.builder().id(8).username("bob").saltedPassword("hash").build();
        when(userRepository.findById(8)).thenReturn(Optional.of(user));
        when(roleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> manageUserRolesService.overwriteUserRoles(8, List.of(99)));
    }
}

