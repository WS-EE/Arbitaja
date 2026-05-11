package com.arbitaja.refactored.backend.pam.adapter.in.web.role;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.pam.adapter.in.web.PamWebE2EBase;
import com.arbitaja.refactored.backend.pam.adapter.in.web.role.dto.request.AddPermissionToRoleRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.role.dto.request.CreateRoleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = "arbitaja.mode=hex")
@AutoConfigureMockMvc
@Transactional
class RoleControllerV2E2ETests extends PamWebE2EBase {

    @Test
    @WithMockUser(username = "admin")
    void getAllRolesEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name=='admin')]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin")
    void getRoleByIdEndpointSuccess() throws Exception {
        Integer adminRoleId = roleJpaRepository.findByName("admin").orElseThrow().getId();

        mockMvc.perform(get("/v2/roles/{id}", adminRoleId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(adminRoleId))
            .andExpect(jsonPath("$.name").value("admin"));
    }

    @Test
    @WithMockUser(username = "admin")
    void getRoleByNameEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/roles/name/admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("admin"));
    }

    @Test
    @WithMockUser(username = "admin")
    void getRolesByUserIdEndpointSuccess() throws Exception {
        Integer adminId = userJpaRepository.findByUsername("admin").orElseThrow().getId();

        mockMvc.perform(get("/v2/roles/user/{userId}", adminId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name=='admin')]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin")
    void createRoleEndpointSuccess() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest(unique("role"), List.of());

        mockMvc.perform(post("/v2/roles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(request.name()));
    }

    @Test
    @WithMockUser(username = "admin")
    void updateRoleEndpointSuccess() throws Exception {
        CreateRoleRequest createRequest = new CreateRoleRequest(unique("role_update"), List.of());

        String createResponse = mockMvc.perform(post("/v2/roles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Integer roleId = objectMapper.readTree(createResponse).get("id").asInt();
        CreateRoleRequest updateRequest = new CreateRoleRequest(unique("role_edited"), List.of());

        mockMvc.perform(put("/v2/roles/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(roleId))
            .andExpect(jsonPath("$.name").value(updateRequest.name()));
    }

    @Test
    @WithMockUser(username = "admin")
    void overwriteRolePermissionsEndpointSuccess() throws Exception {
        CreateRoleRequest createRequest = new CreateRoleRequest(unique("role_perm"), List.of());

        String createResponse = mockMvc.perform(post("/v2/roles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Integer roleId = objectMapper.readTree(createResponse).get("id").asInt();

        mockMvc.perform(put("/v2/roles/{id}/permissions", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AddPermissionToRoleRequest(List.of()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(roleId));
    }
}

