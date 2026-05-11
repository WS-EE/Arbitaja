package com.arbitaja.refactored.backend.scoring.adapter.util;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.request.ScoringCriterionUpsertRequest;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.response.ScoringCriterionResponse;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.ManageScoringCriterionUseCase;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ScoringCriterionWebMapper {

    public ManageScoringCriterionUseCase.UpsertScoringCriterionCommand toCommand(ScoringCriterionUpsertRequest request) {
        return ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
            .name(request.name())
            .description(request.description())
            .manual(request.manual())
            .totalPoints(request.totalPoints())
            .generalized(request.generalized())
            .expectedResult(request.expectedResult())
            .template(request.template())
            .visibilityLevel(request.visibilityLevel())
            .scoringHostId(request.scoringHostId())
            .criteriaTemplateId(request.criteriaTemplateId())
            .competitionId(request.competitionId())
            .build();
    }

    public ScoringCriterionResponse toResponse(ScoringCriterion criterion) {
        return new ScoringCriterionResponse(
            criterion.getId(),
            criterion.getName(),
            criterion.getDescription(),
            criterion.getManual(),
            criterion.getTotalPoints(),
            criterion.getGeneralized(),
            criterion.getExpectedResult(),
            criterion.getTemplate(),
            criterion.getVisibilityLevel(),
            criterion.getScoringHostId(),
            criterion.getCriteriaTemplateId()
        );
    }
}
