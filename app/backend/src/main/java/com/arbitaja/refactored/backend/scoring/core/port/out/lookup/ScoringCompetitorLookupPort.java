package com.arbitaja.refactored.backend.scoring.core.port.out.lookup;

import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetitor;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Output port for loading the competitor projection scoring needs.
 */
public interface ScoringCompetitorLookupPort {

    Optional<ScoringCompetitor> findById(@NonNull Integer competitorId);

    List<ScoringCompetitor> findByCompetitionId(@NonNull Integer competitionId);

    boolean isCompetitorInCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId);
}
