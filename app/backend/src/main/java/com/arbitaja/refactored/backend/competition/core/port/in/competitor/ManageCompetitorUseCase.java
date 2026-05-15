package com.arbitaja.refactored.backend.competition.core.port.in.competitor;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import lombok.*;

/**
 * Input port for creating and editing competitors.
 */
public interface ManageCompetitorUseCase {

    Competitor createCompetitor(@NonNull UpsertCompetitorCommand command);

    Competitor updateCompetitor(@NonNull Integer id, @NonNull UpsertCompetitorCommand command);

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UpsertCompetitorCommand {
        @NonNull
        private String alias;
        @NonNull
        private Integer publicDisplayNameType;
        private Integer personalDataId;
        private String fullName;
        private String email;
        private Integer schoolId;
        private Integer competitionId;
    }
}

