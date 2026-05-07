package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.annotations.RequiresScoringPermission;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CompetitionScoringCriteriaResultsResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CompetitorCriteriaResultsResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.ScoringDashboardResponse;
import com.arbitaja.refactored.backend.scoring.adapter.util.ScoringDashboardWebMapper;
import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import com.arbitaja.refactored.backend.scoring.core.port.in.dashboard.GetScoringDashboardUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.in.security.CheckScoringPermissionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound REST adapter for the scoring dashboard.
 *
 * <p>Mirrors the legacy admin/competitor split:
 *
 * <ul>
 *     <li>Admins always see the full history.</li>
 *     <li>Other authenticated viewers only see entries up to the competition's
 *         {@code score_showtime} and only when the competition has published its scores.</li>
 * </ul>
 */
@RestController
@RequestMapping("/v2/scoring/dashboard")
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "arbitaja.scoring.mode", havingValue = "hex", matchIfMissing = true)
public class ScoringDashboardControllerV2 {

    private final GetScoringDashboardUseCase getScoringDashboardUseCase;
    private final CheckScoringPermissionUseCase checkScoringPermissionUseCase;
    private final ScoringDashboardWebMapper mapper;

    @GetMapping("/competition/{competitionId}/history")
    @RequiresScoringPermission(ScoringPermissionCode.VIEW_SCORING_DASHBOARD)
    public ResponseEntity<ScoringDashboardResponse> getDashboard(@PathVariable Integer competitionId) {
        log.info("Getting scoring dashboard for competition {}", competitionId);
        boolean isAdmin = currentViewerIsAdmin();
        return ResponseEntity.ok(mapper.toResponse(getScoringDashboardUseCase.getDashboard(competitionId, isAdmin)));
    }

    @GetMapping("/competition/{competitionId}/criteria")
    @RequiresScoringPermission(ScoringPermissionCode.VIEW_SCORING_DASHBOARD)
    public ResponseEntity<CompetitionScoringCriteriaResultsResponse> getCriteriaResults(@PathVariable Integer competitionId) {
        log.info("Getting scoring criteria dashboard for competition {}", competitionId);
        boolean isAdmin = currentViewerIsAdmin();
        return ResponseEntity.ok(mapper.toResponse(
            getScoringDashboardUseCase.getCriteriaResultsForCompetition(competitionId, isAdmin)
        ));
    }

    @GetMapping("/competition/{competitionId}/criteria/competitor/{competitorId}")
    @RequiresScoringPermission(ScoringPermissionCode.VIEW_SCORING_DASHBOARD)
    public ResponseEntity<CompetitorCriteriaResultsResponse> getCriteriaResultsForCompetitor(
        @PathVariable Integer competitionId,
        @PathVariable Integer competitorId
    ) {
        log.info("Getting scoring criteria for competition {} competitor {}", competitionId, competitorId);
        boolean isAdmin = currentViewerIsAdmin();
        return ResponseEntity.ok(mapper.toResponse(
            getScoringDashboardUseCase.getCriteriaResultsForCompetitor(competitionId, competitorId, isAdmin)
        ));
    }

    private boolean currentViewerIsAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return false;
        }
        return checkScoringPermissionUseCase.userIsAdmin(auth.getName());
    }
}
