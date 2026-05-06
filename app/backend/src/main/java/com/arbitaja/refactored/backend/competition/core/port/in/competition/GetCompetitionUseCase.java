package com.arbitaja.refactored.backend.competition.core.port.in.competition;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import lombok.NonNull;

import java.util.List;

/**
 * Input port for querying competitions.
 */
public interface GetCompetitionUseCase {

    List<Competition> getAllCompetitions();

    Competition getCompetitionById(@NonNull Integer id);

    Competition getCompetitionByName(@NonNull String name);
}

