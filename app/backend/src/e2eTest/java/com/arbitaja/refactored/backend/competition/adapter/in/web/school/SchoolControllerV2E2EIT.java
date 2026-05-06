package com.arbitaja.refactored.backend.competition.adapter.in.web.school;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.competition.adapter.in.web.CompetitionWebE2EBase;
import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.request.SchoolUpsertRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = {
    "arbitaja.pam.mode=hex",
    "arbitaja.competition.mode=hex"
})
@AutoConfigureMockMvc
@Transactional
class SchoolControllerV2E2EIT extends CompetitionWebE2EBase {

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getAllSchoolsEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/schools"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name=='RandomSchool')]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getSchoolByIdEndpointSuccess() throws Exception {
        Integer schoolId = firstSchoolId();

        mockMvc.perform(get("/v2/schools/{id}", schoolId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(schoolId))
            .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void createSchoolEndpointSuccess() throws Exception {
        String schoolName = unique("school");
        SchoolUpsertRequest request = new SchoolUpsertRequest(schoolName);

        mockMvc.perform(post("/v2/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(schoolName));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void updateSchoolEndpointSuccess() throws Exception {
        Integer schoolId = firstSchoolId();
        String newName = unique("updated_school");
        SchoolUpsertRequest request = new SchoolUpsertRequest(newName);

        mockMvc.perform(put("/v2/schools/{id}", schoolId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(schoolId))
            .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void deleteSchoolEndpointSuccess() throws Exception {
        String schoolName = unique("delete_school");
        SchoolUpsertRequest createRequest = new SchoolUpsertRequest(schoolName);

        String response = mockMvc.perform(post("/v2/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Integer schoolId = objectMapper.readTree(response).get("id").asInt();

        mockMvc.perform(delete("/v2/schools/{id}", schoolId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("School deleted successfully"));

        assertTrue(schoolJpaRepository.findById(schoolId).isEmpty());
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void getAllSchoolsWithoutPermissionThrowsForbidden() throws Exception {
        mockMvc.perform(get("/v2/schools"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "noRolesTestUser")
    void createSchoolWithoutPermissionThrowsForbidden() throws Exception {
        SchoolUpsertRequest request = new SchoolUpsertRequest("Unauthorized School");

        mockMvc.perform(post("/v2/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void getSchoolByIdNotFoundThrowsNotFound() throws Exception {
        mockMvc.perform(get("/v2/schools/{id}", 99999))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"admin"})
    void createSchoolWithDuplicateNameThrowsConflict() throws Exception {
        String schoolName = unique("school_dup");

        // First create
        SchoolUpsertRequest request1 = new SchoolUpsertRequest(schoolName);
        mockMvc.perform(post("/v2/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated());

        // Try duplicate
        SchoolUpsertRequest request2 = new SchoolUpsertRequest(schoolName);
        mockMvc.perform(post("/v2/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isConflict());
    }
}

