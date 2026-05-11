package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PersonalDataJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.PersonalDataJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.port.out.personaldata.PersonalDataRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Optional;

/**
 * Persistence adapter implementing PersonalDataRepositoryPort.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class PersonalDataPersistenceAdapter implements PersonalDataRepositoryPort {

    private final PersonalDataJpaRepository personalDataJpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public Optional<PersonalData> findById(@NonNull Integer id) {
        return personalDataJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public PersonalData save(@NonNull PersonalData personalData) {
        PersonalDataJpaEntity entity = mapper.toEntity(personalData);
        PersonalDataJpaEntity savedEntity = personalDataJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(@NonNull PersonalData personalData) {
        personalDataJpaRepository.deleteById(personalData.getId());
    }
}

