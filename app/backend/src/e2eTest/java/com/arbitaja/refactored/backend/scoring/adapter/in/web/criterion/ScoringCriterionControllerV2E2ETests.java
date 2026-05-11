package com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.ScoringWebE2EBase;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.request.ScoringCriterionUpsertRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = {
    "arbitaja.mode=hex"
})
@AutoConfigureMockMvc
@Transactional
class ScoringCriterionControllerV2E2ETests extends ScoringWebE2EBase {

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getAllScoringCriteriaSuccess() throws Exception {
        mockMvc.perform(get("/v2/scoring/criteria"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void createScoringCriterionAndLinkToCompetition() throws Exception {
        ScoringCriterionUpsertRequest request = new ScoringCriterionUpsertRequest(
            unique("crit"), "description", true, 25.0, false, "expected", false, 0, null, null, firstCompetitionId()
        );

        mockMvc.perform(post("/v2/scoring/criteria")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(request.name()))
            .andExpect(jsonPath("$.total_points").value(25.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getScoringCriterionByIdSuccess() throws Exception {
        Integer id = createCriterionLinkedToCompetition(10.0);

        mockMvc.perform(get("/v2/scoring/criteria/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getByIdReturns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/v2/scoring/criteria/99999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getByCompetitionReturnsLinkedCriteria() throws Exception {
        Integer createdId = createCriterionLinkedToCompetition(10.0);

        mockMvc.perform(get("/v2/scoring/criteria/by-competition/{id}", firstCompetitionId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id==" + createdId + ")]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void updateScoringCriterionSuccess() throws Exception {
        Integer id = createCriterionLinkedToCompetition(10.0);
        String updatedName = unique("updated_crit");

        ScoringCriterionUpsertRequest update = new ScoringCriterionUpsertRequest(
            updatedName, "updated", false, 15.0, false, "exp", false, 0, null, null, null
        );

        mockMvc.perform(put("/v2/scoring/criteria/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(updatedName))
            .andExpect(jsonPath("$.total_points").value(15.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void deleteScoringCriterionSuccess() throws Exception {
        Integer id = createCriterionLinkedToCompetition(10.0);

        mockMvc.perform(delete("/v2/scoring/criteria/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Scoring criterion deleted successfully"));
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void getAllScoringCriteriaWithoutPermissionForbidden() throws Exception {
        mockMvc.perform(get("/v2/scoring/criteria"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void createScoringCriterionWithMissingCompetitionReturns404() throws Exception {
        ScoringCriterionUpsertRequest request = new ScoringCriterionUpsertRequest(
            unique("crit"), "description", true, 25.0, false, "expected", false, 0, null, null, 99999
        );

        mockMvc.perform(post("/v2/scoring/criteria")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }
}
