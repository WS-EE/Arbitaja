package com.arbitaja.refactored.backend.competition.adapter.in.web.competitor;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.competition.adapter.in.web.CompetitionWebE2EBase;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.request.CompetitorUpsertRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = {
    "arbitaja.mode=hex"
})
@AutoConfigureMockMvc
@Transactional
class CompetitorControllerV2E2ETests extends CompetitionWebE2EBase {

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getAllCompetitorsEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/competitor"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCompetitorByIdEndpointSuccess() throws Exception {
        Integer competitorId = firstCompetitorId();

        mockMvc.perform(get("/v2/competitor/{id}", competitorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(competitorId))
            .andExpect(jsonPath("$.alias").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCompetitorsByCompetitionIdEndpointSuccess() throws Exception {
        Integer competitionId = firstCompetitionId();

        mockMvc.perform(get("/v2/competitor/competition/{competitionId}", competitionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void createCompetitorEndpointSuccess() throws Exception {
        CompetitorUpsertRequest request = new CompetitorUpsertRequest(
            unique("competitor_alias"),
            1,
            null,
            "New Competitor",
            "competitor@example.com",
            firstSchoolId()
        );

        mockMvc.perform(post("/v2/competitor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.alias").value(request.alias()))
            .andExpect(jsonPath("$.public_display_name_type").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void editCompetitorEndpointSuccess() throws Exception {
        Integer competitorId = firstCompetitorId();

        CompetitorUpsertRequest request = new CompetitorUpsertRequest(
            unique("edited_alias"),
            2,
            null,
            "Edited Competitor",
            "edited@example.com",
            firstSchoolId()
        );

        mockMvc.perform(put("/v2/competitor/{id}", competitorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(competitorId))
            .andExpect(jsonPath("$.alias").value(request.alias()))
            .andExpect(jsonPath("$.public_display_name_type").value(2));
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void getCompetitorWithoutPermissionThrowsForbidden() throws Exception {
        Integer competitorId = firstCompetitorId();

        mockMvc.perform(get("/v2/competitor/{id}", competitorId))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void createCompetitorWithoutPermissionThrowsForbidden() throws Exception {
        CompetitorUpsertRequest request = new CompetitorUpsertRequest(
            "unauthorized",
            1,
            null,
            "Unauthorized",
            "unauth@example.com",
            firstSchoolId()
        );

        mockMvc.perform(post("/v2/competitor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCompetitorByIdNotFoundThrowsNotFound() throws Exception {
        mockMvc.perform(get("/v2/competitor/{id}", 99999))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void createCompetitorWithDuplicateAliasThrowsConflict() throws Exception {
        String alias = unique("dup_alias");

        // First create
        CompetitorUpsertRequest request1 = new CompetitorUpsertRequest(
            alias, 1, null, "First", "first@example.com", firstSchoolId()
        );

        mockMvc.perform(post("/v2/competitor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());

        // Try duplicate
        CompetitorUpsertRequest request2 = new CompetitorUpsertRequest(
            alias, 1, null, "Second", "second@example.com", firstSchoolId()
        );

        mockMvc.perform(post("/v2/competitor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isConflict());
    }
}

