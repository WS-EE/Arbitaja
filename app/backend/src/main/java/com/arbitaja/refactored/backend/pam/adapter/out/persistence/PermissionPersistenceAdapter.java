package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PermissionJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.PermissionJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence adapter implementing PermissionRepositoryPort.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class PermissionPersistenceAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public Optional<Permission> findById(@NonNull Integer id) {
        return permissionJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findByUserId(@NonNull Integer userId) {
        return permissionJpaRepository.findByUserId(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Permission save(@NonNull Permission permission) {
        var entity = mapper.toEntity(permission);
        var savedEntity = permissionJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(@NonNull Permission permission) {
        permissionJpaRepository.deleteById(permission.getId());
    }

    @Override
    public boolean userHasPermissions(@NonNull Integer userId, @NonNull PermissionCode[] requiredPermissions) {
        List<PermissionJpaEntity> permissions = permissionJpaRepository.findByUserId(userId);
        return Arrays.stream(requiredPermissions)
            .allMatch(required -> permissions.stream()
                .anyMatch(p -> p.getKey().equals(required.name())));
    }
}

