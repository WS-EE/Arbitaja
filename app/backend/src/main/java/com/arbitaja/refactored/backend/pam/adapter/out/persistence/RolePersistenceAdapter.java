package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RoleJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.RoleJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence adapter implementing RoleRepositoryPort.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class RolePersistenceAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public Optional<Role> findById(@NonNull Integer id) {
        return roleJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(@NonNull String name) {
        return roleJpaRepository.findByName(name)
            .map(mapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Role> findByUserId(@NonNull Integer userId) {
        return roleJpaRepository.findByUserId(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Role save(@NonNull Role role) {
        RoleJpaEntity entity = mapper.toEntity(role);
        RoleJpaEntity savedEntity = roleJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(@NonNull Role role) {
        roleJpaRepository.deleteById(role.getId());
    }
}

