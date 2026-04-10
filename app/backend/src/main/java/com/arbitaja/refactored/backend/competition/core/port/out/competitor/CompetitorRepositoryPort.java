package com.arbitaja.refactored.backend.competition.core.port.out.competitor;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import java.util.Set;
import lombok.NonNull;

import java.util.Optional;

/**
 * Output port for competitor persistence.
 */
public interface CompetitorRepositoryPort {

	Optional<Competitor> findById(@NonNull Integer id);

	Set<Competitor> getAllCompetitors();

	boolean existsByAlias(@NonNull String alias);

	boolean existsByAliasAndIdNot(@NonNull String alias, @NonNull Integer id);

	Competitor save(@NonNull Competitor competitor);
}

