package com.arbitaja.refactored.backend.competition.core.application.competition;

import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.port.in.competition.GetCompetitionUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionCompetitorQueryPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionRepositoryPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service for competition read operations.
 */
@Service
@RequiredArgsConstructor
public class GetCompetitionService implements GetCompetitionUseCase {

    private final CompetitionRepositoryPort competitionRepository;
    private final CompetitionCompetitorQueryPort competitionCompetitorQuery;

    @Override
    public List<Competition> getAllCompetitions() {
        return competitionRepository.findAll().stream().map(this::enrichWithCompetitors).toList();
    }

    @Override
    public Competition getCompetitionById(@NonNull Integer id) {
        Competition competition = competitionRepository.findById(id)
            .orElseThrow(() -> EntityNotFoundException.competition(id));
        return enrichWithCompetitors(competition);
    }

    @Override
    public Competition getCompetitionByName(@NonNull String name) {
        Competition competition = competitionRepository.findByName(name)
            .orElseThrow(() -> EntityNotFoundException.competitionByName(name));
        return enrichWithCompetitors(competition);
    }

    private Competition enrichWithCompetitors(Competition competition) {
        competition.setCompetitors(competitionCompetitorQuery.findByCompetitionId(competition.getId()));
        return competition;
    }
}

