package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.RolePermissionJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RolePermissionRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter implementing RolePermissionRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class RolePermissionPersistenceAdapter implements RolePermissionRepositoryPort {

    private final RolePermissionJpaRepository rolePermissionJpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public Optional<RolePermission> findById(@NonNull Integer id) {
        return rolePermissionJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<RolePermission> findAll() {
        return rolePermissionJpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<RolePermission> findByRoleId(@NonNull Integer roleId) {
        return rolePermissionJpaRepository.findByRoleId(roleId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public RolePermission save(@NonNull RolePermission rolePermission) {
        var entity = mapper.toEntity(rolePermission);
        var savedEntity = rolePermissionJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(@NonNull RolePermission rolePermission) {
        if (rolePermission.getId() != null) {
            rolePermissionJpaRepository.deleteById(rolePermission.getId());
            return;
        }
        rolePermissionJpaRepository.delete(mapper.toEntity(rolePermission));
    }

    @Override
    public void deleteById(@NonNull Integer id) {
        rolePermissionJpaRepository.deleteById(id);
    }
}

