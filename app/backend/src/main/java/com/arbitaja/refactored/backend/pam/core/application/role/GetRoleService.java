package com.arbitaja.refactored.backend.pam.core.application.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.port.in.role.GetRoleUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementing role query use cases.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class GetRoleService implements GetRoleUseCase {

    private final RoleRepositoryPort roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(@NonNull Integer id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(@NonNull String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<Role> getRolesByUserId(@NonNull Integer userId) {
        return roleRepository.findByUserId(userId);
    }

    @Override
    public Page<Role> getRolesPaged(String search, Pageable pageable) {
        return roleRepository.findPaged(search, pageable);
    }
}

