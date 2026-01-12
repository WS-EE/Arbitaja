package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.*;
import com.arbitaja.refactored.backend.pam.core.port.in.user.GetUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.permission.PermissionRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service implementing user query use cases.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserService implements GetUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;

    @Override
    public Optional<User> getUserById(@NonNull Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(@NonNull String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserProfile(@NonNull Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> EntityNotFoundException.user(userId));

    }
}

