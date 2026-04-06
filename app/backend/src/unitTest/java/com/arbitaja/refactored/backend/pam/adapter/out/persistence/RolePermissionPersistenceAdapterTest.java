package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PermissionJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RoleJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RolePermissionJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.RolePermissionJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolePermissionPersistenceAdapterTest {

    @Mock
    private RolePermissionJpaRepository rolePermissionJpaRepository;

    @Mock
    private PersistenceMapper mapper;

    @InjectMocks
    private RolePermissionPersistenceAdapter adapter;

    @Test
    void findByIdMapsEntityToDomain() {
        RolePermissionJpaEntity entity = RolePermissionJpaEntity.builder()
            .id(10)
            .permission(PermissionJpaEntity.builder().id(1).name("View users").key("VIEW_USERS").build())
            .role(RoleJpaEntity.builder().id(2).name("admin").build())
            .build();
        RolePermission domain = RolePermission.builder()
            .id(10)
            .permission(Permission.builder().id(1).name("View users").key("VIEW_USERS").build())
            .role(Role.builder().id(2).name("admin").build())
            .build();

        when(rolePermissionJpaRepository.findById(10)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<RolePermission> result = adapter.findById(10);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getId());
    }

    @Test
    void findAllMapsEachEntity() {
        RolePermissionJpaEntity first = RolePermissionJpaEntity.builder()
            .id(1)
            .permission(PermissionJpaEntity.builder().id(1).name("View users").key("VIEW_USERS").build())
            .role(RoleJpaEntity.builder().id(1).name("admin").build())
            .build();
        RolePermissionJpaEntity second = RolePermissionJpaEntity.builder()
            .id(2)
            .permission(PermissionJpaEntity.builder().id(2).name("View roles").key("VIEW_ROLES").build())
            .role(RoleJpaEntity.builder().id(1).name("admin").build())
            .build();

        when(rolePermissionJpaRepository.findAll()).thenReturn(List.of(first, second));
        when(mapper.toDomain(first)).thenReturn(RolePermission.builder().id(1).permission(Permission.builder().name("View users").key("VIEW_USERS").build()).role(Role.builder().name("admin").build()).build());
        when(mapper.toDomain(second)).thenReturn(RolePermission.builder().id(2).permission(Permission.builder().name("View roles").key("VIEW_ROLES").build()).role(Role.builder().name("admin").build()).build());

        List<RolePermission> result = adapter.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findByRoleIdMapsRepositoryResults() {
        RolePermissionJpaEntity entity = RolePermissionJpaEntity.builder()
            .id(3)
            .permission(PermissionJpaEntity.builder().id(3).name("Edit users").key("EDIT_USERS").build())
            .role(RoleJpaEntity.builder().id(99).name("manager").build())
            .build();

        when(rolePermissionJpaRepository.findByRoleId(99)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(RolePermission.builder()
            .id(3)
            .permission(Permission.builder().id(3).name("Edit users").key("EDIT_USERS").build())
            .role(Role.builder().id(99).name("manager").build())
            .build());

        List<RolePermission> result = adapter.findByRoleId(99);

        assertEquals(1, result.size());
        assertEquals(3, result.getFirst().getId());
    }

    @Test
    void saveMapsDomainToEntityAndBack() {
        RolePermission domain = RolePermission.builder()
            .permission(Permission.builder().name("View users").key("VIEW_USERS").build())
            .role(Role.builder().name("admin").build())
            .build();
        RolePermissionJpaEntity entity = RolePermissionJpaEntity.builder()
            .permission(PermissionJpaEntity.builder().name("View users").key("VIEW_USERS").build())
            .role(RoleJpaEntity.builder().name("admin").build())
            .build();
        RolePermissionJpaEntity savedEntity = RolePermissionJpaEntity.builder()
            .id(11)
            .permission(PermissionJpaEntity.builder().id(1).name("View users").key("VIEW_USERS").build())
            .role(RoleJpaEntity.builder().id(1).name("admin").build())
            .build();
        RolePermission savedDomain = RolePermission.builder()
            .id(11)
            .permission(Permission.builder().id(1).name("View users").key("VIEW_USERS").build())
            .role(Role.builder().id(1).name("admin").build())
            .build();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(rolePermissionJpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        RolePermission result = adapter.save(domain);

        assertEquals(11, result.getId());
    }

    @Test
    void deleteUsesDeleteByIdWhenIdExists() {
        RolePermission rolePermission = RolePermission.builder()
            .id(123)
            .permission(Permission.builder().name("View users").key("VIEW_USERS").build())
            .role(Role.builder().name("admin").build())
            .build();

        adapter.delete(rolePermission);

        verify(rolePermissionJpaRepository).deleteById(123);
        verify(rolePermissionJpaRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deleteUsesMappedEntityWhenIdMissing() {
        RolePermission rolePermission = RolePermission.builder()
            .permission(Permission.builder().name("View users").key("VIEW_USERS").build())
            .role(Role.builder().name("admin").build())
            .build();
        RolePermissionJpaEntity entity = RolePermissionJpaEntity.builder()
            .permission(PermissionJpaEntity.builder().name("View users").key("VIEW_USERS").build())
            .role(RoleJpaEntity.builder().name("admin").build())
            .build();

        when(mapper.toEntity(rolePermission)).thenReturn(entity);

        adapter.delete(rolePermission);

        verify(rolePermissionJpaRepository).delete(entity);
        verify(rolePermissionJpaRepository, never()).deleteById(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void deleteByIdDelegatesToRepository() {
        adapter.deleteById(55);

        verify(rolePermissionJpaRepository).deleteById(55);
    }
}

