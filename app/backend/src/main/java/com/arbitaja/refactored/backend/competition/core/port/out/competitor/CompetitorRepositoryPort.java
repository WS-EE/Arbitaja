package com.arbitaja.refactored.backend.competition.core.port.out.competitor;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

/**
 * Output port for competitor persistence.
 */
public interface CompetitorRepositoryPort {

	Optional<Competitor> findById(@NonNull Integer id);

	Set<Competitor> getAllCompetitors();

	boolean existsByAlias(@NonNull String alias);

	boolean existsByAliasAndIdNot(@NonNull String alias, @NonNull Integer id);

	Competitor save(@NonNull Competitor competitor);

    Page<Competitor> findPaged(String search, Pageable pageable);
}

