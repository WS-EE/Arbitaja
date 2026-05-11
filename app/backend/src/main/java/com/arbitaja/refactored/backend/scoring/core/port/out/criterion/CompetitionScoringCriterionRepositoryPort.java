package com.arbitaja.refactored.backend.scoring.core.port.out.criterion;

import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import lombok.NonNull;

import java.util.List;

/**
 * Output port for persistence of competition-criterion links.
 */
public interface CompetitionScoringCriterionRepositoryPort {

    List<ScoringCriterion> findCriteriaForCompetition(@NonNull Integer competitionId);

    void linkCriterionToCompetition(@NonNull Integer competitionId, @NonNull Integer criterionId);
}
