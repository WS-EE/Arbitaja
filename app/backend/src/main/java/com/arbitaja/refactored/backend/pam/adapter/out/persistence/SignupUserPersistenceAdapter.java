package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.SignupUserJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.SignupUserJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.port.out.user.SignupUserRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence adapter implementing SignupUserRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class SignupUserPersistenceAdapter implements SignupUserRepositoryPort {

    private final SignupUserJpaRepository signupUserJpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public Optional<SignupUser> findById(@NonNull Integer id) {
        return signupUserJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<SignupUser> findByUsername(@NonNull String username) {
        return signupUserJpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public List<SignupUser> findAll() {
        return signupUserJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public SignupUser save(@NonNull SignupUser signupUser) {
        SignupUserJpaEntity entity = mapper.toEntity(signupUser);
        SignupUserJpaEntity savedEntity = signupUserJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(@NonNull SignupUser signupUser) {
        signupUserJpaRepository.deleteById(signupUser.getId());
    }

    @Override
    public boolean existsByUsername(@NonNull String username) {
        return signupUserJpaRepository.existsByUsername(username);
    }
}

