package com.arbitaja.refactored.backend.scoring.core.port.in.criterion;

import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import lombok.NonNull;

import java.util.List;

/**
 * Input port for querying scoring criteria.
 */
public interface GetScoringCriterionUseCase {

    List<ScoringCriterion> getAllScoringCriteria();

    ScoringCriterion getScoringCriterionById(@NonNull Integer id);

    List<ScoringCriterion> getScoringCriteriaForCompetition(@NonNull Integer competitionId);
}
