package com.arbitaja.refactored.backend.scoring.adapter.util;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CompetitionScoringCriteriaResultsResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CompetitorCriteriaResultsResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CriterionResultResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.ScoringDashboardResponse;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitionScoringCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorDashboard;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CriterionResult;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringDashboard;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class ScoringDashboardWebMapper {

    public ScoringDashboardResponse toResponse(ScoringDashboard dashboard) {
        Set<ScoringDashboardResponse.CompetitorDashboardResponse> competitors = new LinkedHashSet<>();
        for (CompetitorDashboard competitor : dashboard.getCompetitors()) {
            competitors.add(new ScoringDashboardResponse.CompetitorDashboardResponse(
                competitor.getCompetitorName(),
                competitor.getTotalScore(),
                competitor.getResults().stream()
                    .map(point -> new ScoringDashboardResponse.DashboardResultPointResponse(
                        point.getTimestamp(),
                        point.getPointAmount()
                    ))
                    .toList()
            ));
        }
        return new ScoringDashboardResponse(competitors);
    }

    public CompetitionScoringCriteriaResultsResponse toResponse(CompetitionScoringCriteriaResults results) {
        Set<CompetitorCriteriaResultsResponse> competitors = new LinkedHashSet<>();
        for (CompetitorCriteriaResults competitor : results.getCompetitors()) {
            competitors.add(toResponse(competitor));
        }
        return new CompetitionScoringCriteriaResultsResponse(
            results.getCompetitionId(),
            results.getCompetitionName(),
            competitors
        );
    }

    public CompetitorCriteriaResultsResponse toResponse(CompetitorCriteriaResults competitor) {
        Set<CriterionResultResponse> criteria = new LinkedHashSet<>();
        for (CriterionResult criterion : competitor.getCriteria()) {
            criteria.add(new CriterionResultResponse(
                criterion.getCriterionId(),
                criterion.getCriterionName(),
                criterion.getPoints()
            ));
        }
        return new CompetitorCriteriaResultsResponse(
            competitor.getCompetitorId(),
            competitor.getCompetitorName(),
            criteria
        );
    }
}
