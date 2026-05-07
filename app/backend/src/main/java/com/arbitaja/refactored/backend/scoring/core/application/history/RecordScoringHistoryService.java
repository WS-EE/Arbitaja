package com.arbitaja.refactored.backend.scoring.core.application.history;

import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.ValidationException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCompetition;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import com.arbitaja.refactored.backend.scoring.core.port.in.history.RecordScoringHistoryUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.ScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.history.ScoringHistoryRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitorLookupPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;

/**
 * Application service for recording new scoring history entries.
 *
 * <p>Mirrors {@code CompetitionScoringService.addCompetitionScoringCriteriaHistory}
 * from the legacy module: validates that the competition exists and is active, that
 * the criterion exists, the points are within the criterion's bounds, and that the
 * competitor is enrolled in the competition.</p>
 */
@Service
@RequiredArgsConstructor
public class RecordScoringHistoryService implements RecordScoringHistoryUseCase {

    private final ScoringCompetitionLookupPort competitionLookup;
    private final ScoringCompetitorLookupPort competitorLookup;
    private final ScoringCriterionRepositoryPort scoringCriterionRepository;
    private final ScoringHistoryRepositoryPort scoringHistoryRepository;
    private final Clock clock;

    @Override
    @Transactional
    public ScoringHistoryEntry recordScore(@NonNull RecordScoringCommand command) {
        ScoringCompetition competition = competitionLookup.findById(command.getCompetitionId())
            .orElseThrow(() -> EntityNotFoundException.competition(command.getCompetitionId()));
        ScoringCriterion criterion = scoringCriterionRepository.findById(command.getScoringCriterionId())
            .orElseThrow(() -> EntityNotFoundException.scoringCriterion(command.getScoringCriterionId()));

        if (criterion.getTotalPoints() != null
            && (command.getPoints() < 0 || command.getPoints() > criterion.getTotalPoints())) {
            throw ValidationException.pointsOutOfRange(criterion.getTotalPoints());
        }

        if (competitorLookup.findById(command.getCompetitorId()).isEmpty()) {
            throw EntityNotFoundException.competitor(command.getCompetitorId());
        }

        if (!competitorLookup.isCompetitorInCompetition(command.getCompetitionId(), command.getCompetitorId())) {
            throw EntityNotFoundException.competitorInCompetition(command.getCompetitionId(), command.getCompetitorId());
        }

        Instant now = Instant.now(clock);
        if (!competition.isActiveAt(now)) {
            throw ValidationException.competitionNotActive();
        }

        ScoringHistoryEntry entry = ScoringHistoryEntry.builder()
            .competitionId(command.getCompetitionId())
            .competitorId(command.getCompetitorId())
            .scoringCriterionId(command.getScoringCriterionId())
            .scoringCriterionName(criterion.getName())
            .pointsGiven(command.getPoints())
            .createdAt(Timestamp.from(now))
            .build();

        return scoringHistoryRepository.save(entry);
    }
}
