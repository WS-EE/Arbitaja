package com.arbitaja.refactored.backend.competition.core.port.out.competition;

import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionCompetitor;
import lombok.NonNull;

import java.util.Set;

/**
 * Output port for reading competitors in a competition.
 */
public interface CompetitionCompetitorQueryPort {

    Set<CompetitionCompetitor> findByCompetitionId(@NonNull Integer competitionId);
}

