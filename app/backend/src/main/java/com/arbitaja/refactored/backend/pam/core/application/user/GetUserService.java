package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.in.user.GetUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
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
 * Application service implementing user query use cases.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class GetUserService implements GetUserUseCase {

    private final UserRepositoryPort userRepository;

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

    @Override
    public Page<User> getUsersPaged(String search, Pageable pageable) {
        return userRepository.findPaged(search, pageable);
    }
}

