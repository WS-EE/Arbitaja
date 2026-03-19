package com.arbitaja.refactored.backend.pam.core.application.role;

import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.port.in.role.CreateRoleUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Application service implementing role creation and update use cases.
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CreateRoleService implements CreateRoleUseCase {

    private final RoleRepositoryPort roleRepository;

    @Override
    public Role createRole(RoleCommand command) {
        log.info("Creating new role: {}", command.name());
        
        Role role = Role.createNew(command.name());
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Integer id, RoleCommand command) {
        log.info("Updating role with id: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        
        role.setName(command.name());
        role.setChangedAt(new Timestamp(System.currentTimeMillis()));
        
        return roleRepository.save(role);
    }
}

