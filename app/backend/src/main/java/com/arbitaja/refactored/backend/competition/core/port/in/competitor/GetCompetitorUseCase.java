package com.arbitaja.refactored.backend.competition.core.port.in.competitor;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * Use case for retrieving competitor information.
 * This interface defines the contracts for accessing competitor data from competitions.
 */
public interface GetCompetitorUseCase {

  /**
   * Retrieves a specific competitor by their ID.
   *
   * @param id the competitor ID to retrieve from
   * @return the competitor with the specified ID
   * @throws EntityNotFoundException if the competitor with the given ID is not found
   */
  Competitor getCompetitorById(@NonNull Integer id);

  /**
   * Retrieves all competitors registered for a specific competition.
   *
   * @param id the competition ID to retrieve competitors from
   * @return a set of competitors in the specified competition (may be empty)
   */
  Set<Competitor> getCompetitorsByCompetitionId(@NonNull Integer id);

  /**
   * Retrieves all competitors across all competitions.
   *
   * @return a set of all competitors in the system (may be empty)
   */
  Set<Competitor> getAllCompetitors();

  Page<Competitor> getCompetitorsPaged(String search, Pageable pageable);
}
