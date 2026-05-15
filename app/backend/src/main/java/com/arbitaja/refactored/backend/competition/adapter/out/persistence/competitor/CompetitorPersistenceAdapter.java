package com.arbitaja.refactored.backend.competition.adapter.out.persistence.competitor;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorPersonalDataJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorSchoolJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.mapper.CompetitorPersistenceMapper;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorPersonalDataJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorSchoolJpaRepository;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import com.arbitaja.refactored.backend.competition.core.port.out.competitor.CompetitorRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Persistence adapter for competitor management.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitorPersistenceAdapter implements CompetitorRepositoryPort {

    private final CompetitorJpaRepository competitorRepository;
    private final CompetitorPersonalDataJpaRepository personalDataRepository;
    private final CompetitorSchoolJpaRepository schoolRepository;
    private final CompetitorPersistenceMapper mapper;

    @Override
    public Optional<com.arbitaja.refactored.backend.competition.core.domain.model.Competitor> findById(@NonNull Integer id) {
        return competitorRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Set<Competitor> getAllCompetitors() {
        return competitorRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean existsByAlias(@NonNull String alias) {
        return competitorRepository.findByAlias(alias) != null;
    }

    @Override
    public boolean existsByAliasAndIdNot(@NonNull String alias, @NonNull Integer id) {
        CompetitorJpaEntity competitor = competitorRepository.findByAlias(alias);
        return competitor != null && !id.equals(competitor.getId());
    }

    @Override
    @Transactional
    public com.arbitaja.refactored.backend.competition.core.domain.model.Competitor save(@NonNull com.arbitaja.refactored.backend.competition.core.domain.model.Competitor competitor) {
        CompetitorJpaEntity entity = mapper.toEntity(competitor);
        entity.setPersonalData(resolvePersonalData(competitor.getPersonalData()));
        return mapper.toDomain(competitorRepository.save(entity));
    }

    @Override
    public Page<Competitor> findPaged(String search, Pageable pageable) {
        String s = search == null ? "" : search;
        return competitorRepository.findBySearchTerm(s, pageable)
            .map(mapper::toDomain);
    }

    private CompetitorPersonalDataJpaEntity resolvePersonalData(com.arbitaja.refactored.backend.competition.core.domain.model.CompetitorPersonalData personalData) {
        if (personalData == null) {
            throw new IllegalArgumentException("Personal data is required");
        }

        if (personalData.getId() != null) {
            return personalDataRepository.findById(personalData.getId())
                .orElseThrow(() -> EntityNotFoundException.personalData(personalData.getId()));
        }

        if (personalData.getFullName() == null || personalData.getFullName().isBlank()
            || personalData.getEmail() == null || personalData.getEmail().isBlank()) {
            throw new IllegalArgumentException("fullName and email are required when personalDataId is not provided");
        }

        CompetitorSchoolJpaEntity school = null;
        if(personalData.getSchool() != null) {
            if (personalData.getSchool().getId() != null) {
                school = schoolRepository.findById(personalData.getSchool().getId())
                    .orElseThrow(() -> EntityNotFoundException.school(personalData.getSchool().getId()));
            } else if (personalData.getSchool().getName() != null && !personalData.getSchool().getName().isBlank()) {
                school = schoolRepository.findByName(personalData.getSchool().getName())
                    .orElseGet(() -> schoolRepository.save(CompetitorSchoolJpaEntity.builder()
                        .name(personalData.getSchool().getName())
                        .build()));
            } else {
                throw new IllegalArgumentException("schoolId or schoolName is required when personalDataId is not provided");
            }
        }

        return personalDataRepository.save(mapper.toNewPersonalDataEntity(personalData, school));
    }
}



