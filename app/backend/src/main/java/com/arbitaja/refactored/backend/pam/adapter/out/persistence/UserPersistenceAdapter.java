package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.UserJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.PersonalDataJpaRepository;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.UserJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence adapter implementing UserRepositoryPort.
 * Translates between domain models and JPA entities.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final PersonalDataJpaRepository personalDataJpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public Optional<User> findById(@NonNull Integer id) {
        return userJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(@NonNull String username) {
        return userJpaRepository.findByUsername(username)
            .map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public User save(@NonNull User user) {
        UserJpaEntity entity = mapper.toEntity(user);

        // Reattach existing personal data via managed reference to avoid transient association errors on persist.
        if (user.getPersonalData() != null && user.getPersonalData().getId() != null) {
            entity.setPersonalData(personalDataJpaRepository.getReferenceById(user.getPersonalData().getId()));
        }

        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(@NonNull User user) {
        userJpaRepository.deleteById(user.getId());
    }

    @Override
    public boolean existsByUsername(@NonNull String username) {
        return userJpaRepository.existsByUsername(username);
    }
}

