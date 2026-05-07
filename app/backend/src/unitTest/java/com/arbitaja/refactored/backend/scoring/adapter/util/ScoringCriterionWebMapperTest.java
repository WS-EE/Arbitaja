package com.arbitaja.refactored.backend.scoring.adapter.util;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.request.ScoringCriterionUpsertRequest;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.response.ScoringCriterionResponse;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.ManageScoringCriterionUseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringCriterionWebMapperTest {

    private final ScoringCriterionWebMapper mapper = new ScoringCriterionWebMapper();

    @Test
    void toCommandCopiesAllFields() {
        ScoringCriterionUpsertRequest request = new ScoringCriterionUpsertRequest(
            "speed", "desc", true, 10.0, false, "expected", false, 1, 5, 6, 7
        );

        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command = mapper.toCommand(request);

        assertEquals("speed", command.getName());
        assertEquals("desc", command.getDescription());
        assertTrue(command.getManual());
        assertEquals(10.0, command.getTotalPoints());
        assertEquals(7, command.getCompetitionId());
        assertEquals(6, command.getCriteriaTemplateId());
        assertEquals(5, command.getScoringHostId());
    }

    @Test
    void toResponseCopiesAllFields() {
        ScoringCriterion criterion = ScoringCriterion.builder()
            .id(1).name("speed").description("desc")
            .manual(true).totalPoints(10.0).generalized(false)
            .expectedResult("expected").template(false)
            .visibilityLevel(2).scoringHostId(3).criteriaTemplateId(4)
            .build();

        ScoringCriterionResponse response = mapper.toResponse(criterion);

        assertEquals(1, response.id());
        assertEquals("speed", response.name());
        assertEquals(10.0, response.totalPoints());
        assertEquals(2, response.visibilityLevel());
    }
}
