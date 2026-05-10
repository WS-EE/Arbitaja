package com.arbitaja.refactored.backend.pam.core.application.permission;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CreatePermissionUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;


@Service
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CreatePermissionService implements CreatePermissionUseCase {

    private final PermissionRepositoryPort permissionRepositoryPort;

    @Override
    @Transactional
    public Permission createPermission(CreatePermissionUseCase.PermissionCommand command) {
        log.info("Creating permission with name: {}", command.name());

        Permission permission = Permission.builder()
            .name(command.name())
            .key(command.key())
            .build();

        return permissionRepositoryPort.save(permission);

    }

    @Override
    @Transactional
    public Permission updatePermission(Integer id, PermissionCommand command) {
        log.info("Updating permission with id: {}", id);

        Permission existingPermission = permissionRepositoryPort.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Permission not found with id: " + id));

        existingPermission.setName(command.name());
        existingPermission.setKey(command.key());

        return permissionRepositoryPort.save(existingPermission);
    }
}
