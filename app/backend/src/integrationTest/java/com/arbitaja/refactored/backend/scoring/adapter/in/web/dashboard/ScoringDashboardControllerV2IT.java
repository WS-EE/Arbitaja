package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.ScoringExceptionHandler;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CompetitionScoringCriteriaResultsResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.CompetitorCriteriaResultsResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard.dto.response.ScoringDashboardResponse;
import com.arbitaja.refactored.backend.scoring.adapter.util.ScoringDashboardWebMapper;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitionScoringCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.CompetitorCriteriaResults;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringDashboard;
import com.arbitaja.refactored.backend.scoring.core.port.in.dashboard.GetScoringDashboardUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.in.security.CheckScoringPermissionUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedHashSet;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ScoringDashboardControllerV2IT {

    @Mock
    private GetScoringDashboardUseCase getScoringDashboardUseCase;

    @Mock
    private CheckScoringPermissionUseCase checkScoringPermissionUseCase;

    @Mock
    private ScoringDashboardWebMapper mapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ScoringDashboardControllerV2 controller = new ScoringDashboardControllerV2(
            getScoringDashboardUseCase, checkScoringPermissionUseCase, mapper
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ScoringExceptionHandler())
            .build();
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("alice", "pw")
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getDashboardReturnsResponseFromUseCase() throws Exception {
        ScoringDashboard domain = ScoringDashboard.builder().competitionId(1).competitionName("Final").build();
        ScoringDashboardResponse response = new ScoringDashboardResponse(new LinkedHashSet<>());

        when(getScoringDashboardUseCase.getDashboard(1, false)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.perform(get("/v2/scoring/dashboard/competition/1/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.competitors").isArray());
    }

    @Test
    void getDashboardReturns404WhenCompetitionMissing() throws Exception {
        when(getScoringDashboardUseCase.getDashboard(404, false))
            .thenThrow(EntityNotFoundException.competition(404));

        mockMvc.perform(get("/v2/scoring/dashboard/competition/404/history"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getCriteriaResultsReturnsForbiddenWhenScoresUnpublished() throws Exception {
        when(checkScoringPermissionUseCase.userIsAdmin("alice")).thenReturn(false);
        when(getScoringDashboardUseCase.getCriteriaResultsForCompetition(eq(1), eq(false)))
            .thenThrow(ForbiddenException.scoresNotPublished());

        mockMvc.perform(get("/v2/scoring/dashboard/competition/1/criteria"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void getCriteriaResultsReturnsMappedResponse() throws Exception {
        CompetitionScoringCriteriaResults domain = CompetitionScoringCriteriaResults.builder()
            .competitionId(1).competitionName("Final").build();
        CompetitionScoringCriteriaResultsResponse response =
            new CompetitionScoringCriteriaResultsResponse(1, "Final", new LinkedHashSet<>());

        when(checkScoringPermissionUseCase.userIsAdmin("alice")).thenReturn(true);
        when(getScoringDashboardUseCase.getCriteriaResultsForCompetition(1, true)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.perform(get("/v2/scoring/dashboard/competition/1/criteria"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.competition_id").value(1));
    }

    @Test
    void getCriteriaResultsForCompetitorReturnsMappedResponse() throws Exception {
        CompetitorCriteriaResults domain = CompetitorCriteriaResults.builder()
            .competitorId(10).competitorName("alice").build();
        CompetitorCriteriaResultsResponse response =
            new CompetitorCriteriaResultsResponse(10, "alice", new LinkedHashSet<>());

        when(checkScoringPermissionUseCase.userIsAdmin("alice")).thenReturn(true);
        when(getScoringDashboardUseCase.getCriteriaResultsForCompetitor(1, 10, true)).thenReturn(domain);
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.perform(get("/v2/scoring/dashboard/competition/1/criteria/competitor/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.competitor_id").value(10))
            .andExpect(jsonPath("$.name").value("alice"));
    }
}
