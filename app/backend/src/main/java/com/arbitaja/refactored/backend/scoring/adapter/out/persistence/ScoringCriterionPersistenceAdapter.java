package com.arbitaja.refactored.backend.scoring.adapter.out.persistence;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.ScoringCriterionJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.mapper.ScoringPersistenceMapper;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository.ScoringCriterionJpaRepository;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.ScoringCriterionRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter for scoring criterion storage.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ScoringCriterionPersistenceAdapter implements ScoringCriterionRepositoryPort {

    private final ScoringCriterionJpaRepository scoringCriterionRepository;
    private final ScoringPersistenceMapper mapper;

    @Override
    public List<ScoringCriterion> findAll() {
        return scoringCriterionRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ScoringCriterion> findById(@NonNull Integer id) {
        return scoringCriterionRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public ScoringCriterion save(@NonNull ScoringCriterion criterion) {
        ScoringCriterionJpaEntity entity = mapper.toEntity(criterion);
        ScoringCriterionJpaEntity saved = scoringCriterionRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(@NonNull Integer id) {
        scoringCriterionRepository.deleteById(id);
    }
}
