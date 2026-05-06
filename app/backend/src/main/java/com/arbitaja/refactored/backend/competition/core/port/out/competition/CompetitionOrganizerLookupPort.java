package com.arbitaja.refactored.backend.competition.core.port.out.competition;

import com.arbitaja.refactored.backend.competition.core.domain.model.CompetitionOrganizer;
import lombok.NonNull;

import java.util.Optional;

/**
 * Output port to resolve organizer identities from external user data.
 */
public interface CompetitionOrganizerLookupPort {

    Optional<CompetitionOrganizer> findById(@NonNull Integer id);

    Optional<CompetitionOrganizer> findByUsername(@NonNull String username);
}

