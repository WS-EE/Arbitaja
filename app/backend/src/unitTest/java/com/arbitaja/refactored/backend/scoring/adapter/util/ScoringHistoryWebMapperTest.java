package com.arbitaja.refactored.backend.scoring.adapter.util;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.request.AddScoringHistoryRequest;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.response.ScoringHistoryEntryResponse;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import com.arbitaja.refactored.backend.scoring.core.port.in.history.RecordScoringHistoryUseCase;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoringHistoryWebMapperTest {

    private final ScoringHistoryWebMapper mapper = new ScoringHistoryWebMapper();

    @Test
    void toCommandCopiesAllFields() {
        AddScoringHistoryRequest request = new AddScoringHistoryRequest(1, 2, 3, 4.5);

        RecordScoringHistoryUseCase.RecordScoringCommand command = mapper.toCommand(request);

        assertEquals(1, command.getCompetitionId());
        assertEquals(2, command.getCompetitorId());
        assertEquals(3, command.getScoringCriterionId());
        assertEquals(4.5, command.getPoints());
    }

    @Test
    void toResponseCopiesAllFields() {
        Timestamp now = Timestamp.valueOf("2026-04-06 12:00:00");
        ScoringHistoryEntry entry = ScoringHistoryEntry.builder()
            .id(99).competitionId(1).competitorId(2)
            .scoringCriterionId(3).scoringCriterionName("ssh")
            .pointsGiven(5.0).createdAt(now)
            .build();

        ScoringHistoryEntryResponse response = mapper.toResponse(entry);

        assertEquals(99, response.id());
        assertEquals(1, response.competitionId());
        assertEquals(2, response.competitorId());
        assertEquals(3, response.criteriaId());
        assertEquals("ssh", response.criteriaName());
        assertEquals(5.0, response.pointsGiven());
        assertEquals(now, response.createdAt());
    }
}
