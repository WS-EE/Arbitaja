package com.arbitaja.refactored.backend.competition.core.port.out.competition;

import lombok.NonNull;

/**
 * Output port for managing competitor membership in competitions.
 */
public interface CompetitionMembershipPort {

    boolean existsCompetitor(@NonNull Integer competitorId);

    boolean isCompetitorInCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId);

    void addCompetitorToCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId);

    void removeCompetitorFromCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId);
}

