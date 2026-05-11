package com.arbitaja.refactored.backend.scoring.core.port.in.criterion;

import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Input port for mutating scoring criteria.
 */
public interface ManageScoringCriterionUseCase {

    ScoringCriterion createScoringCriterion(@NonNull UpsertScoringCriterionCommand command);

    ScoringCriterion updateScoringCriterion(@NonNull Integer id, @NonNull UpsertScoringCriterionCommand command);

    void deleteScoringCriterion(@NonNull Integer id);

    void addScoringCriterionToCompetition(@NonNull Integer competitionId, @NonNull Integer criterionId);

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UpsertScoringCriterionCommand {
        @NonNull
        private String name;
        private String description;
        private Boolean manual;
        @NonNull
        private Double totalPoints;
        private Boolean generalized;
        private String expectedResult;
        private Boolean template;
        private Integer visibilityLevel;
        private Integer scoringHostId;
        private Integer criteriaTemplateId;
        private Integer competitionId;
    }
}
