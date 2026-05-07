package com.arbitaja.refactored.backend.scoring.adapter.in.web.dashboard;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.ScoringWebE2EBase;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.request.AddScoringHistoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = {
    "arbitaja.pam.mode=hex",
    "arbitaja.competition.mode=hex",
    "arbitaja.scoring.mode=hex"
})
@AutoConfigureMockMvc
@Transactional
class ScoringDashboardControllerV2E2ETests extends ScoringWebE2EBase {

    private void recordScore(Integer criterionId, double points) throws Exception {
        AddScoringHistoryRequest request = new AddScoringHistoryRequest(
            firstCompetitionId(), firstCompetitorId(), criterionId, points
        );
        mockMvc.perform(post("/v2/scoring/history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getDashboardComputesRunningTotalAfterScoringEvents() throws Exception {
        ensureFirstCompetitionIsActive();
        Integer criterionA = createCriterionLinkedToCompetition(50.0);
        Integer criterionB = createCriterionLinkedToCompetition(50.0);

        recordScore(criterionA, 10.0);
        recordScore(criterionB, 5.0);
        recordScore(criterionA, 20.0);

        mockMvc.perform(get("/v2/scoring/dashboard/competition/{id}/history", firstCompetitionId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.competition_id").value(firstCompetitionId()))
            .andExpect(jsonPath("$.competitors[0].total_score").value(25.0))
            .andExpect(jsonPath("$.competitors[0].results.length()").value(3));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCriteriaResultsForCompetitionShowsLatestPerCriterion() throws Exception {
        ensureFirstCompetitionIsActive();
        Integer criterionA = createCriterionLinkedToCompetition(50.0);
        Integer criterionB = createCriterionLinkedToCompetition(50.0);

        recordScore(criterionA, 10.0);
        recordScore(criterionA, 30.0);
        recordScore(criterionB, 20.0);

        mockMvc.perform(get("/v2/scoring/dashboard/competition/{id}/criteria", firstCompetitionId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.competitors[0].criteria[?(@.criterion_id==" + criterionA + ")].points").value(30.0))
            .andExpect(jsonPath("$.competitors[0].criteria[?(@.criterion_id==" + criterionB + ")].points").value(20.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCriteriaResultsForCompetitorReturnsZeroForUnscoredCriteria() throws Exception {
        ensureFirstCompetitionIsActive();
        Integer criterionA = createCriterionLinkedToCompetition(50.0);
        Integer criterionB = createCriterionLinkedToCompetition(50.0);

        recordScore(criterionA, 10.0);

        mockMvc.perform(get("/v2/scoring/dashboard/competition/{competitionId}/criteria/competitor/{competitorId}",
                firstCompetitionId(), firstCompetitorId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.criteria[?(@.criterion_id==" + criterionA + ")].points").value(10.0))
            .andExpect(jsonPath("$.criteria[?(@.criterion_id==" + criterionB + ")].points").value(0.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getDashboardForUnknownCompetitionReturns404() throws Exception {
        mockMvc.perform(get("/v2/scoring/dashboard/competition/99999/history"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void getDashboardWithoutPermissionForbidden() throws Exception {
        mockMvc.perform(get("/v2/scoring/dashboard/competition/{id}/history", firstCompetitionId()))
            .andExpect(status().isForbidden());
    }
}
