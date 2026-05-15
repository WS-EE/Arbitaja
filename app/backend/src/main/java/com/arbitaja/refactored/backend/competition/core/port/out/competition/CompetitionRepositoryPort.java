package com.arbitaja.refactored.backend.competition.core.port.out.competition;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Output port for persistence operations on competitions.
 */
public interface CompetitionRepositoryPort {

    List<Competition> findAll();

    Optional<Competition> findById(@NonNull Integer id);

    Optional<Competition> findByName(@NonNull String name);

    boolean existsByName(@NonNull String name);

    boolean existsByNameAndIdNot(@NonNull String name, @NonNull Integer id);

    Competition save(@NonNull Competition competition);

    void deleteById(@NonNull Integer id);

    Page<Competition> findPaged(String search, Pageable pageable);

    Page<Competition> findPaged(String search, String status, Pageable pageable);
}

