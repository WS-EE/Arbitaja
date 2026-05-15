package com.arbitaja.refactored.backend.competition.core.port.in.competition;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Input port for querying competitions.
 */
public interface GetCompetitionUseCase {

    List<Competition> getAllCompetitions();

    Competition getCompetitionById(@NonNull Integer id);

    Competition getCompetitionByName(@NonNull String name);

    Page<Competition> getCompetitionsPaged(String search, Pageable pageable);

    Page<Competition> getCompetitionsPaged(String search, String status, Pageable pageable);
}

