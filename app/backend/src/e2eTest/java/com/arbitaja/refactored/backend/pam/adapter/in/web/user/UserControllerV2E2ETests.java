package com.arbitaja.refactored.backend.pam.adapter.in.web.user;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.pam.adapter.in.web.PamWebE2EBase;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.ChangePasswordRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.OverwriteUserRolesRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.UpdateUserRequest;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = "arbitaja.pam.mode=hex")
@AutoConfigureMockMvc
@Transactional
class UserControllerV2E2ETests extends PamWebE2EBase {

    @Test
    @WithMockUser(username = "admin")
    void createUserEndpointSuccess() throws Exception {
        SignupRequest request = SignupRequest.builder()
            .username(unique("create"))
            .password("pass123")
            .fullName("Create User")
            .email(unique("create") + "@example.com")
            .schoolId(firstSchoolId())
            .build();

        mockMvc.perform(post("/v2/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupPayloadJson(
                    request.getUsername(),
                    request.getPassword(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getSchoolId()
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.username").value(request.getUsername()))
            .andExpect(jsonPath("$.personal_data.full_name").value(request.getFullName()));
    }

    @Test
    @WithMockUser(username = "admin")
    void getAllUsersEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.username=='admin')]").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin")
    void getUserByIdEndpointSuccess() throws Exception {
        Integer adminId = userJpaRepository.findByUsername("admin").orElseThrow().getId();

        mockMvc.perform(get("/v2/user/{id}", adminId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(adminId))
            .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @WithMockUser(username = "admin")
    void updateUserEndpointSuccess() throws Exception {
        CreatedUser created = createUserThroughApi("update");

        UpdateUserRequest request = UpdateUserRequest.builder()
            .username(unique("edited"))
            .fullName("Edited Name")
            .email(unique("edited") + "@example.com")
            .schoolId(firstSchoolId())
            .build();

        mockMvc.perform(put("/v2/user/{id}", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(created.id()))
            .andExpect(jsonPath("$.username").value(request.getUsername()))
            .andExpect(jsonPath("$.personal_data.email").value(request.getEmail()));
    }

    @Test
    @WithMockUser(username = "admin")
    void overwriteUserRolesEndpointSuccess() throws Exception {
        CreatedUser created = createUserThroughApi("roles");
        Integer adminRoleId = roleJpaRepository.findByName("admin").orElseThrow().getId();

        mockMvc.perform(put("/v2/user/{id}/roles", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OverwriteUserRolesRequest(List.of(adminRoleId)))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(created.id()));

        UserJpaEntity updated = userJpaRepository.findById(created.id()).orElseThrow();
        boolean hasAdminRole = updated.getUserRoles().stream()
            .anyMatch(userRole -> "admin".equals(userRole.getRole().getName()));
        assertTrue(hasAdminRole);
    }

    @Test
    @WithMockUser(username = "admin")
    void deleteUserEndpointSuccess() throws Exception {
        CreatedUser created = createUserThroughApi("delete");

        mockMvc.perform(delete("/v2/user/{id}", created.id()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User deleted successfully"));

        assertTrue(userJpaRepository.findById(created.id()).isEmpty());
    }

    @Test
    @WithMockUser(username = "admin")
    void getUserAuthEndpointSuccess() throws Exception {
        mockMvc.perform(get("/v2/user/auth"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @WithMockUser(username = "admin")
    void changePasswordEndpointSuccess() throws Exception {
        CreatedUser created = createUserThroughApi("pwd");
        ChangePasswordRequest request = new ChangePasswordRequest(created.password(), "newPassword456");

        mockMvc.perform(put("/v2/user/change-password/{id}", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Password changed successfully"));

        UserJpaEntity changed = userJpaRepository.findById(created.id()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword456", changed.getSaltedPassword()));
    }
}

