package com.arbitaja.refactored.backend.pam.adapter.in.web.permission;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.pam.adapter.in.web.PamWebE2EBase;
import com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.request.CreatePermissionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = "arbitaja.pam.mode=hex")
@AutoConfigureMockMvc
@Transactional
class PermissionControllerV2E2ETests extends PamWebE2EBase {

    @Test
    @WithMockUser(username = "admin")
    void getAllPermissionsEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/permissions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.key=='VIEW_USERS')]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin")
    void getPermissionByIdEndpointSuccess() throws Exception {
        Integer permissionId = permissionIdByKey("VIEW_USERS");

        mockMvc.perform(get("/v2/permissions/{id}", permissionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(permissionId))
            .andExpect(jsonPath("$.key").value("VIEW_USERS"));
    }

    @Test
    @WithMockUser(username = "admin")
    void getPermissionsByUserIdEndpointSuccess() throws Exception {
        Integer adminId = userJpaRepository.findByUsername("admin").orElseThrow().getId();

        mockMvc.perform(get("/v2/permissions/user/{userId}", adminId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.key=='VIEW_USERS')]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin")
    void createPermissionEndpointSuccess() throws Exception {
        CreatePermissionRequest request = new CreatePermissionRequest("E2E Permission", uniquePermissionKey("E2E_PERM"));

        mockMvc.perform(post("/v2/permissions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(request.name()))
            .andExpect(jsonPath("$.key").value(request.key()));
    }

    @Test
    @WithMockUser(username = "admin")
    void updatePermissionEndpointSuccess() throws Exception {
        CreatePermissionRequest createRequest = new CreatePermissionRequest("Initial Permission", uniquePermissionKey("E2E_UPDATE"));

        String createResponse = mockMvc.perform(post("/v2/permissions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Integer permissionId = objectMapper.readTree(createResponse).get("id").asInt();
        CreatePermissionRequest updateRequest = new CreatePermissionRequest("Updated Permission", uniquePermissionKey("E2E_UPDATED"));

        mockMvc.perform(put("/v2/permissions/update/{id}", permissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(permissionId))
            .andExpect(jsonPath("$.name").value(updateRequest.name()))
            .andExpect(jsonPath("$.key").value(updateRequest.key()));
    }
}

