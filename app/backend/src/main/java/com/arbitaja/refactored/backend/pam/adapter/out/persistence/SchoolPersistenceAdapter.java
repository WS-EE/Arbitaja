package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.SchoolJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.SchoolJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import com.arbitaja.refactored.backend.pam.core.port.out.school.SchoolRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence adapter implementing SchoolRepositoryPort.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class SchoolPersistenceAdapter implements SchoolRepositoryPort {

    private final SchoolJpaRepository schoolJpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public Optional<School> findById(@NonNull Integer id) {
        return schoolJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<School> findAll() {
        return schoolJpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public School save(@NonNull School school) {
        SchoolJpaEntity entity = mapper.toEntity(school);
        SchoolJpaEntity savedEntity = schoolJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(@NonNull School school) {
        schoolJpaRepository.deleteById(school.getId());
    }
}

