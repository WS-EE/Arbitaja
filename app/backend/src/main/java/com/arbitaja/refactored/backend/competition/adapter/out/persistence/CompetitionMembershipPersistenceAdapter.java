package com.arbitaja.refactored.backend.competition.adapter.out.persistence;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorCompetitionJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitorJpaEntity;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitionJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorCompetitionJpaRepository;
import com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository.CompetitorJpaRepository;
import com.arbitaja.refactored.backend.competition.core.port.out.competition.CompetitionMembershipPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Persistence adapter for competition-competitor membership mutations.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitionMembershipPersistenceAdapter implements CompetitionMembershipPort {

    private final CompetitionJpaRepository competitionRepository;
    private final CompetitorJpaRepository competitorRepository;
    private final CompetitorCompetitionJpaRepository competitorCompetitionRepository;

    @Override
    public boolean existsCompetitor(@NonNull Integer competitorId) {
        return competitorRepository.existsById(competitorId);
    }

    @Override
    public boolean isCompetitorInCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId) {
        CompetitionJpaEntity competition = competitionRepository.findById(competitionId).orElse(null);
        CompetitorJpaEntity competitor = competitorRepository.findById(competitorId).orElse(null);
        if (competition == null || competitor == null) {
            return false;
        }
        return competitorCompetitionRepository.findByCompetitorAndCompetition(competitor, competition) != null;
    }

    @Override
    public void addCompetitorToCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId) {
        CompetitionJpaEntity competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> new IllegalArgumentException("Competition not found with id: " + competitionId));
        CompetitorJpaEntity competitor = competitorRepository.findById(competitorId)
            .orElseThrow(() -> new IllegalArgumentException("Competitor not found with id: " + competitorId));

        competitorCompetitionRepository.save(CompetitorCompetitionJpaEntity.builder()
            .competitor(competitor)
            .competition(competition)
            .build());
    }

    @Override
    public void removeCompetitorFromCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId) {
        CompetitionJpaEntity competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> new IllegalArgumentException("Competition not found with id: " + competitionId));
        CompetitorJpaEntity competitor = competitorRepository.findById(competitorId)
            .orElseThrow(() -> new IllegalArgumentException("Competitor not found with id: " + competitorId));

        CompetitorCompetitionJpaEntity link = competitorCompetitionRepository.findByCompetitorAndCompetition(competitor, competition);
        if (link != null) {
            competitorCompetitionRepository.delete(link);
        }
    }
}

