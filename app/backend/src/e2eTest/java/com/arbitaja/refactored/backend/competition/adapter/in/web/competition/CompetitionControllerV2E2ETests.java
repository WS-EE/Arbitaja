package com.arbitaja.refactored.backend.competition.adapter.in.web.competition;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.competition.adapter.in.web.CompetitionWebE2EBase;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.request.CompetitionUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.request.OverwriteCompetitionCompetitorsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = {
    "arbitaja.pam.mode=hex",
    "arbitaja.competition.mode=hex"
})
@AutoConfigureMockMvc
@Transactional
class CompetitionControllerV2E2ETests extends CompetitionWebE2EBase {

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getAllCompetitionsEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/competition"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name=='Noor meister')]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCompetitionByIdEndpointSuccess() throws Exception {
        Integer competitionId = firstCompetitionId();

        mockMvc.perform(get("/v2/competition/{id}", competitionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(competitionId))
            .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCompetitionByNameEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/competition/by-name")
                .param("name", "Noor meister"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Noor meister"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void createCompetitionEndpointSuccess() throws Exception {
        Timestamp startTime = futureTimestamp(1);
        Timestamp endTime = futureTimestamp(5);

        CompetitionUpsertRequest request = new CompetitionUpsertRequest(
            unique("comp"),
            startTime,
            endTime,
            futureTimestamp(4),
            false,
            firstOrganizerId()
        );

        mockMvc.perform(post("/v2/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(request.name()))
            .andExpect(jsonPath("$.organizer.id").value(firstOrganizerId()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void updateCompetitionEndpointSuccess() throws Exception {
        Integer competitionId = firstCompetitionId();
        String newName = unique("updated_comp");

        CompetitionUpsertRequest request = new CompetitionUpsertRequest(
            newName,
            futureTimestamp(1),
            futureTimestamp(5),
            futureTimestamp(4),
            true,
            firstOrganizerId()
        );

        mockMvc.perform(put("/v2/competition/{id}", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(competitionId))
            .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void addCompetitorToCompetitionEndpointSuccess() throws Exception {
        CompetitionUpsertRequest createRequest = new CompetitionUpsertRequest(
            unique("comp_add_competitor"),
            futureTimestamp(1),
            futureTimestamp(5),
            futureTimestamp(4),
            false,
            firstOrganizerId()
        );

        String createResponse = mockMvc.perform(post("/v2/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Integer competitionId = objectMapper.readTree(createResponse).get("id").asInt();
        Integer competitorId = firstCompetitorId();

        mockMvc.perform(post("/v2/competition/{competitionId}/competitors/{competitorId}",
                competitionId, competitorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(competitionId))
            .andExpect(jsonPath("$.competitors[*].id").isArray())
            .andExpect(jsonPath("$.competitors[*].id").value(org.hamcrest.Matchers.hasItem(competitorId)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void removeCompetitorFromCompetitionEndpointSuccess() throws Exception {
        Integer competitionId = firstCompetitionId();
        Integer competitorId = firstCompetitorId();

        mockMvc.perform(delete("/v2/competition/{competitionId}/competitors/{competitorId}",
                competitionId, competitorId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(competitionId));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void overwriteCompetitionCompetitorsEndpointSuccess() throws Exception {
        Integer competitionId = firstCompetitionId();
        List<Integer> competitorIds = List.of(firstCompetitorId());

        mockMvc.perform(put("/v2/competition/{competitionId}/competitors", competitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OverwriteCompetitionCompetitorsRequest(competitorIds))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(competitionId));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void deleteCompetitionEndpointSuccess() throws Exception {
        Timestamp startTime = futureTimestamp(1);
        Timestamp endTime = futureTimestamp(5);

        CompetitionUpsertRequest request = new CompetitionUpsertRequest(
            unique("delete_comp"),
            startTime,
            endTime,
            futureTimestamp(4),
            false,
            firstOrganizerId()
        );

        String response = mockMvc.perform(post("/v2/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Integer competitionId = objectMapper.readTree(response).get("id").asInt();

        mockMvc.perform(delete("/v2/competition/{id}", competitionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Competition deleted successfully"));

        assertTrue(competitionJpaRepository.findById(competitionId).isEmpty());
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void getCompetitionWithoutPermissionThrowsForbidden() throws Exception {
        mockMvc.perform(get("/v2/competition"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void createCompetitionWithoutPermissionThrowsForbidden() throws Exception {
        CompetitionUpsertRequest request = new CompetitionUpsertRequest(
            "unauthorized",
            futureTimestamp(1),
            futureTimestamp(5),
            futureTimestamp(4),
            false,
            firstOrganizerId()
        );

        mockMvc.perform(post("/v2/competition")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCompetitionByNameNotFoundThrowsNotFound() throws Exception {
        mockMvc.perform(get("/v2/competition/by-name")
                .param("name", "nonexistent_competition_xyz"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getCompetitionByIdNotFoundThrowsNotFound() throws Exception {
        mockMvc.perform(get("/v2/competition/{id}", 99999))
            .andExpect(status().isNotFound());
    }
}

