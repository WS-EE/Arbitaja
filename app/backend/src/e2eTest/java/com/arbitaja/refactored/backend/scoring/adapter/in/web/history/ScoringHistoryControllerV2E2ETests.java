package com.arbitaja.refactored.backend.scoring.adapter.in.web.history;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.ScoringWebE2EBase;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.request.AddScoringHistoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = {
    "app.API_KEY=test-e2e-api-key"
})
@AutoConfigureMockMvc
@Transactional
class ScoringHistoryControllerV2E2ETests extends ScoringWebE2EBase {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String TEST_API_KEY = "test-e2e-api-key";

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void recordScoreSuccess() throws Exception {
        ensureFirstCompetitionIsActive();
        Integer criterionId = createCriterionLinkedToCompetition(50.0);

        AddScoringHistoryRequest request = new AddScoringHistoryRequest(
            firstCompetitionId(), firstCompetitorId(), criterionId, 25.0
        );

        mockMvc.perform(post("/v2/scoring/history")
                .header(API_KEY_HEADER, TEST_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.points_given").value(25.0))
            .andExpect(jsonPath("$.competitor_id").value(firstCompetitorId()))
            .andExpect(jsonPath("$.competition_id").value(firstCompetitionId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void recordScoreOutOfRangeReturns400() throws Exception {
        Integer criterionId = createCriterionLinkedToCompetition(50.0);

        AddScoringHistoryRequest request = new AddScoringHistoryRequest(
            firstCompetitionId(), firstCompetitorId(), criterionId, 51.0
        );

        mockMvc.perform(post("/v2/scoring/history")
                .header(API_KEY_HEADER, TEST_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid input"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void recordScoreForNonexistentCompetitionReturns404() throws Exception {
        Integer criterionId = createCriterionLinkedToCompetition(50.0);

        AddScoringHistoryRequest request = new AddScoringHistoryRequest(
            99999, firstCompetitorId(), criterionId, 10.0
        );

        mockMvc.perform(post("/v2/scoring/history")
                .header(API_KEY_HEADER, TEST_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void recordScoreWithoutPermissionForbidden() throws Exception {
        AddScoringHistoryRequest request = new AddScoringHistoryRequest(
            firstCompetitionId(), firstCompetitorId(), 1, 10.0
        );

        mockMvc.perform(post("/v2/scoring/history")
                .header(API_KEY_HEADER, TEST_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }
}
