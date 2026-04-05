package com.arbitaja.refactored.backend.pam.core.application.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRoleServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;

    @InjectMocks
    private GetRoleService getRoleService;

    @Test
    void getAllRolesDelegatesToRepository() {
        when(roleRepository.findAll()).thenReturn(List.of(Role.builder().id(1).name("admin").build()));

        List<Role> result = getRoleService.getAllRoles();

        assertEquals(1, result.size());
        assertEquals("admin", result.getFirst().getName());
    }

    @Test
    void getRoleByIdDelegatesToRepository() {
        when(roleRepository.findById(5)).thenReturn(Optional.of(Role.builder().id(5).name("teacher").build()));

        Optional<Role> result = getRoleService.getRoleById(5);

        assertTrue(result.isPresent());
        assertEquals("teacher", result.get().getName());
    }

    @Test
    void getRoleByNameDelegatesToRepository() {
        when(roleRepository.findByName("user")).thenReturn(Optional.of(Role.builder().id(2).name("user").build()));

        Optional<Role> result = getRoleService.getRoleByName("user");

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getId());
    }

    @Test
    void getRolesByUserIdDelegatesToRepository() {
        when(roleRepository.findByUserId(8)).thenReturn(List.of(Role.builder().id(3).name("judge").build()));

        List<Role> result = getRoleService.getRolesByUserId(8);

        assertEquals(1, result.size());
        assertEquals("judge", result.getFirst().getName());
    }
}

