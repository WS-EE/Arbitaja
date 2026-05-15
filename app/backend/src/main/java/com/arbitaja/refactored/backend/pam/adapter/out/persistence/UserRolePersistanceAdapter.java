package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.UserRoleJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.UserRole;
import com.arbitaja.refactored.backend.pam.core.port.out.role.UserRoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class UserRolePersistanceAdapter implements UserRoleRepositoryPort {

    private final UserRoleJpaRepository userRoleJpaRepository;
    private final PersistenceMapper mapper;


    public UserRole saveUserRole(UserRole userRole) {
        return mapper.toDomain(userRoleJpaRepository.save(mapper.toEntity(userRole)));
    }

    public void deleteUserRole(UserRole userRole) {
        userRoleJpaRepository.delete(mapper.toEntity(userRole));
    }
}
