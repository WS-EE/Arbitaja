package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PermissionJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.PermissionJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionPersistenceAdapterTest {

    @Mock
    private PermissionJpaRepository permissionJpaRepository;

    @Mock
    private PersistenceMapper mapper;

    @InjectMocks
    private PermissionPersistenceAdapter adapter;

    @Test
    void userHasPermissionsReturnsTrueWhenAllPermissionsExist() {
        when(permissionJpaRepository.findByUserId(5)).thenReturn(List.of(
            PermissionJpaEntity.builder().id(1).name("View users").key("VIEW_USERS").build(),
            PermissionJpaEntity.builder().id(2).name("View roles").key("VIEW_ROLES").build()
        ));

        boolean result = adapter.userHasPermissions(5, new PermissionCode[]{PermissionCode.VIEW_USERS, PermissionCode.VIEW_ROLES});

        assertTrue(result);
    }

    @Test
    void userHasPermissionsThrowsWhenAnyRequiredPermissionMissing() {
        when(permissionJpaRepository.findByUserId(5)).thenReturn(List.of(
            PermissionJpaEntity.builder().id(1).name("View users").key("VIEW_USERS").build()
        ));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
            () -> adapter.userHasPermissions(5, new PermissionCode[]{PermissionCode.VIEW_USERS, PermissionCode.EDIT_USERS})
        );

        assertTrue(exception.getMessage().contains("EDIT_USERS"));
    }

    @Test
    void saveMapsToEntityAndBackToDomain() {
        Permission permission = Permission.builder().name("View users").key("VIEW_USERS").build();
        PermissionJpaEntity entity = PermissionJpaEntity.builder().name("View users").key("VIEW_USERS").build();
        PermissionJpaEntity savedEntity = PermissionJpaEntity.builder().id(10).name("View users").key("VIEW_USERS").build();
        Permission savedDomain = Permission.builder().id(10).name("View users").key("VIEW_USERS").build();

        when(mapper.toEntity(permission)).thenReturn(entity);
        when(permissionJpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        Permission result = adapter.save(permission);

        assertEquals(10, result.getId());
    }

    @Test
    void deleteDelegatesToDeleteById() {
        Permission permission = Permission.builder().id(77).name("View users").key("VIEW_USERS").build();

        adapter.delete(permission);

        verify(permissionJpaRepository).deleteById(77);
    }
}

