package com.arbitaja.refactored.backend.scoring.adapter.out.persistence;

import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.entity.CompetitionScoringCriterionJpaEntity;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.mapper.ScoringPersistenceMapper;
import com.arbitaja.refactored.backend.scoring.adapter.out.persistence.repository.CompetitionScoringCriterionJpaRepository;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.CompetitionScoringCriterionRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

/**
 * Persistence adapter for the competition-criterion link.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitionScoringCriterionPersistenceAdapter implements CompetitionScoringCriterionRepositoryPort {

    private final CompetitionScoringCriterionJpaRepository repository;
    private final ScoringPersistenceMapper mapper;

    @Override
    public List<ScoringCriterion> findCriteriaForCompetition(@NonNull Integer competitionId) {
        return repository.findCriteriaByCompetitionId(competitionId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void linkCriterionToCompetition(@NonNull Integer competitionId, @NonNull Integer criterionId) {
        CompetitionScoringCriterionJpaEntity link = CompetitionScoringCriterionJpaEntity.builder()
            .competitionId(competitionId)
            .criteriaId(criterionId)
            .build();
        repository.save(link);
    }
}
