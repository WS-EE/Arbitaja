package com.arbitaja.refactored.backend.competition.core.port.in.school;

import com.arbitaja.refactored.backend.competition.core.domain.model.School;
import lombok.*;

/**
 * Input port for school write operations.
 */
public interface ManageSchoolUseCase {

    School createSchool(@NonNull UpsertSchoolCommand command);

    School updateSchool(@NonNull Integer id, @NonNull UpsertSchoolCommand command);

    void deleteSchool(@NonNull Integer id);

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class UpsertSchoolCommand {
        @NonNull
        private String name;
    }
}

