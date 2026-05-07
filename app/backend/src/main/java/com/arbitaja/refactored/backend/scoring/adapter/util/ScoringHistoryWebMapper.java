package com.arbitaja.refactored.backend.scoring.adapter.util;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.request.AddScoringHistoryRequest;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.response.ScoringHistoryEntryResponse;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import com.arbitaja.refactored.backend.scoring.core.port.in.history.RecordScoringHistoryUseCase;
import org.springframework.stereotype.Component;

@Component
public class ScoringHistoryWebMapper {

    public RecordScoringHistoryUseCase.RecordScoringCommand toCommand(AddScoringHistoryRequest request) {
        return RecordScoringHistoryUseCase.RecordScoringCommand.builder()
            .competitionId(request.competitionId())
            .competitorId(request.competitorId())
            .scoringCriterionId(request.criteriaId())
            .points(request.points())
            .build();
    }

    public ScoringHistoryEntryResponse toResponse(ScoringHistoryEntry entry) {
        return new ScoringHistoryEntryResponse(
            entry.getId(),
            entry.getCompetitionId(),
            entry.getCompetitorId(),
            entry.getScoringCriterionId(),
            entry.getScoringCriterionName(),
            entry.getPointsGiven(),
            entry.getCreatedAt()
        );
    }
}
