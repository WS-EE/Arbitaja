package com.arbitaja.refactored.backend.scoring.core.application.criterion;

import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.GetScoringCriterionUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.CompetitionScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.ScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

/**
 * Application service for scoring criterion read operations.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class GetScoringCriterionService implements GetScoringCriterionUseCase {

    private final ScoringCriterionRepositoryPort scoringCriterionRepository;
    private final CompetitionScoringCriterionRepositoryPort competitionCriterionRepository;
    private final ScoringCompetitionLookupPort competitionLookup;

    @Override
    public List<ScoringCriterion> getAllScoringCriteria() {
        return scoringCriterionRepository.findAll();
    }

    @Override
    public ScoringCriterion getScoringCriterionById(@NonNull Integer id) {
        return scoringCriterionRepository.findById(id)
            .orElseThrow(() -> EntityNotFoundException.scoringCriterion(id));
    }

    @Override
    public List<ScoringCriterion> getScoringCriteriaForCompetition(@NonNull Integer competitionId) {
        if (!competitionLookup.existsById(competitionId)) {
            throw EntityNotFoundException.competition(competitionId);
        }
        return competitionCriterionRepository.findCriteriaForCompetition(competitionId);
    }

    @Override
    public Page<ScoringCriterion> getScoringCriteriaPaged(String search, Pageable pageable) {
        return scoringCriterionRepository.findPaged(search, pageable);
    }
}
