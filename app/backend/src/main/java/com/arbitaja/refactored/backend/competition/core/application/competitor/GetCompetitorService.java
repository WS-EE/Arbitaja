package com.arbitaja.refactored.backend.competition.core.application.competitor;

import com.arbitaja.refactored.backend.competition.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import com.arbitaja.refactored.backend.competition.core.port.in.competitor.GetCompetitorUseCase;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionCompetitorQueryPort;
import com.arbitaja.refactored.backend.competition.core.port.out.competitor.CompetitorRepositoryPort;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class GetCompetitorService implements GetCompetitorUseCase {

  private final CompetitorRepositoryPort competitorRepositoryPort;
  private final CompetitionCompetitorQueryPort competitionCompetitorQueryPort;

    @Override
    public Competitor getCompetitorById(@NonNull Integer id) {
        return competitorRepositoryPort.findById(id).orElseThrow(() -> EntityNotFoundException.competitor(id));
    }

    @Override
    public Set<Competitor> getCompetitorsByCompetitionId(@NonNull Integer id) {
        return competitionCompetitorQueryPort.findByCompetitionId(id).stream()
            .map(competitionCompetitor -> Competitor.builder()
                .id(competitionCompetitor.getId())
                .alias(competitionCompetitor.getAlias())
                .personalData(competitionCompetitor.getPersonalData())
                .publicDisplayNameType(competitionCompetitor.getPublicDisplayNameType())
                .build())
            .collect(Collectors.toSet());

    }

    @Override
    public Set<Competitor> getAllCompetitors() {
        return competitorRepositoryPort.getAllCompetitors()
            .stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }



}
