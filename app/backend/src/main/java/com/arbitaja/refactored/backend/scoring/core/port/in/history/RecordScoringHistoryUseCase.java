package com.arbitaja.refactored.backend.scoring.core.port.in.history;

import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Input port for adding scoring history entries.
 */
public interface RecordScoringHistoryUseCase {

    ScoringHistoryEntry recordScore(@NonNull RecordScoringCommand command);

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class RecordScoringCommand {
        @NonNull
        private Integer competitionId;
        @NonNull
        private Integer competitorId;
        @NonNull
        private Integer scoringCriterionId;
        @NonNull
        private Double points;
    }
}
