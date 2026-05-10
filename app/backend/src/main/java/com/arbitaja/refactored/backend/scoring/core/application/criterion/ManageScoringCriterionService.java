package com.arbitaja.refactored.backend.scoring.core.application.criterion;

import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.ManageScoringCriterionUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.CompetitionScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.criterion.ScoringCriterionRepositoryPort;
import com.arbitaja.refactored.backend.scoring.core.port.out.lookup.ScoringCompetitionLookupPort;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for scoring criterion write operations.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ManageScoringCriterionService implements ManageScoringCriterionUseCase {

    private final ScoringCriterionRepositoryPort scoringCriterionRepository;
    private final CompetitionScoringCriterionRepositoryPort competitionCriterionRepository;
    private final ScoringCompetitionLookupPort competitionLookup;

    @Override
    @Transactional
    public ScoringCriterion createScoringCriterion(@NonNull UpsertScoringCriterionCommand command) {
        if (command.getCompetitionId() != null && !competitionLookup.existsById(command.getCompetitionId())) {
            throw EntityNotFoundException.competition(command.getCompetitionId());
        }
        if (command.getCriteriaTemplateId() != null && scoringCriterionRepository.findById(command.getCriteriaTemplateId()).isEmpty()) {
            throw EntityNotFoundException.scoringCriterion(command.getCriteriaTemplateId());
        }

        ScoringCriterion criterion = toDomain(null, command);
        ScoringCriterion saved = scoringCriterionRepository.save(criterion);

        if (command.getCompetitionId() != null) {
            competitionCriterionRepository.linkCriterionToCompetition(command.getCompetitionId(), saved.getId());
        }
        return saved;
    }

    @Override
    @Transactional
    public ScoringCriterion updateScoringCriterion(@NonNull Integer id, @NonNull UpsertScoringCriterionCommand command) {
        ScoringCriterion existing = scoringCriterionRepository.findById(id)
            .orElseThrow(() -> EntityNotFoundException.scoringCriterion(id));

        if (command.getCriteriaTemplateId() != null && scoringCriterionRepository.findById(command.getCriteriaTemplateId()).isEmpty()) {
            throw EntityNotFoundException.scoringCriterion(command.getCriteriaTemplateId());
        }

        existing.setName(command.getName());
        existing.setDescription(command.getDescription());
        existing.setManual(command.getManual());
        existing.setTotalPoints(command.getTotalPoints());
        existing.setGeneralized(command.getGeneralized());
        existing.setExpectedResult(command.getExpectedResult());
        existing.setTemplate(command.getTemplate());
        existing.setVisibilityLevel(command.getVisibilityLevel());
        existing.setScoringHostId(command.getScoringHostId());
        existing.setCriteriaTemplateId(command.getCriteriaTemplateId());

        return scoringCriterionRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteScoringCriterion(@NonNull Integer id) {
        if (scoringCriterionRepository.findById(id).isEmpty()) {
            throw EntityNotFoundException.scoringCriterion(id);
        }
        scoringCriterionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addScoringCriterionToCompetition(@NonNull Integer competitionId, @NonNull Integer criterionId) {
        if (!competitionLookup.existsById(competitionId)) {
            throw EntityNotFoundException.competition(competitionId);
        }
        if (scoringCriterionRepository.findById(criterionId).isEmpty()) {
            throw EntityNotFoundException.scoringCriterion(criterionId);
        }
        competitionCriterionRepository.linkCriterionToCompetition(competitionId, criterionId);
    }

    private ScoringCriterion toDomain(Integer id, UpsertScoringCriterionCommand command) {
        return ScoringCriterion.builder()
            .id(id)
            .name(command.getName())
            .description(command.getDescription())
            .manual(command.getManual())
            .totalPoints(command.getTotalPoints())
            .generalized(command.getGeneralized())
            .expectedResult(command.getExpectedResult())
            .template(command.getTemplate())
            .visibilityLevel(command.getVisibilityLevel())
            .scoringHostId(command.getScoringHostId())
            .criteriaTemplateId(command.getCriteriaTemplateId())
            .build();
    }
}
