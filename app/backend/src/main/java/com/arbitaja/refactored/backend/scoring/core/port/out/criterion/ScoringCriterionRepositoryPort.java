package com.arbitaja.refactored.backend.scoring.core.port.out.criterion;

import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Output port for persistence operations on scoring criteria.
 */
public interface ScoringCriterionRepositoryPort {

    List<ScoringCriterion> findAll();

    Optional<ScoringCriterion> findById(@NonNull Integer id);

    ScoringCriterion save(@NonNull ScoringCriterion criterion);

    void deleteById(@NonNull Integer id);
}
