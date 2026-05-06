package com.arbitaja.refactored.backend.competition.core.port.in.competition;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * Input port for mutating competition state.
 */
public interface ManageCompetitionUseCase {

    Competition createCompetition(@NonNull UpsertCompetitionCommand command);

    Competition updateCompetition(@NonNull Integer id, @NonNull UpsertCompetitionCommand command);

    Competition addCompetitorToCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId);

    Competition removeCompetitorFromCompetition(@NonNull Integer competitionId, @NonNull Integer competitorId);

    Competition overwriteCompetitionCompetitors(@NonNull Integer competitionId, List<Integer> competitorIds);

    void deleteCompetition(@NonNull Integer id);

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UpsertCompetitionCommand {
        @NonNull
        private String name;
        @NonNull
        private Timestamp startTime;
        @NonNull
        private Timestamp endTime;
        private Timestamp scoreShowtime;
        private Boolean publishScores;
        private Integer organizerId;
    }
}

