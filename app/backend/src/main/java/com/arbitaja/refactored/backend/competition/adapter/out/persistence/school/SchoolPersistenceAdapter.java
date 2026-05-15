package com.arbitaja.refactored.backend.competition.adapter.out.persistence.school;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorSchoolJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper.SchoolPersistenceMapper;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorSchoolJpaRepository;
import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import com.arbitaja.refactored.backend.competition.core.port.out.school.SchoolRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter for school operations.
 */
@Component("competitionSchoolPersistenceAdapter")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class SchoolPersistenceAdapter implements SchoolRepositoryPort {

    private final CompetitorSchoolJpaRepository schoolRepository;
    private final SchoolPersistenceMapper mapper;

    @Override
    public List<School> findAll() {
        return schoolRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<School> findById(@NonNull Integer id) {
        return schoolRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<School> findByName(@NonNull String name) {
        return schoolRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public School save(@NonNull School school) {
        CompetitorSchoolJpaEntity entity = mapper.toEntity(school);

        if (school.getId() == null) {
            entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            schoolRepository.findById(school.getId()).ifPresent(existing -> entity.setCreatedAt(existing.getCreatedAt()));
        }

        return mapper.toDomain(schoolRepository.save(entity));
    }

    @Override
    public void deleteById(@NonNull Integer id) {
        schoolRepository.deleteById(id);
    }

    @Override
    public Page<School> findPaged(String search, Pageable pageable) {
        String s = search == null ? "" : search;
        return schoolRepository.findByNameContainingIgnoreCase(s, pageable)
            .map(mapper::toDomain);
    }
}

