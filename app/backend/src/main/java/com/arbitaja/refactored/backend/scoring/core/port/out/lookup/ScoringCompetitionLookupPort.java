package com.arbitaja.refactored.backend.scoring.core.port.out.lookup;

import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetition;
import lombok.NonNull;

import java.util.Optional;

/**
 * Output port for loading the competition projection scoring needs.
 */
public interface ScoringCompetitionLookupPort {

    Optional<ScoringCompetition> findById(@NonNull Integer competitionId);

    boolean existsById(@NonNull Integer competitionId);
}
