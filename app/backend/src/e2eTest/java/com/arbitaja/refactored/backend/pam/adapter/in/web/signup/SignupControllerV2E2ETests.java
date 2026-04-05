package com.arbitaja.refactored.backend.pam.adapter.in.web.signup;

import com.arbitaja.backend.ArbitajaBackendApplication;
import com.arbitaja.refactored.backend.pam.adapter.in.web.PamWebE2EBase;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.SignupRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ArbitajaBackendApplication.class, properties = "arbitaja.pam.mode=hex")
@AutoConfigureMockMvc
@Transactional
class SignupControllerV2E2ETests extends PamWebE2EBase {

    @Test
    void signupUserEndpointSuccess() throws Exception {
        SignupRequest request = SignupRequest.builder()
            .username(unique("signup"))
            .password("signup123")
            .fullName("Signup User")
            .email(unique("signup") + "@example.com")
            .schoolId(firstSchoolId())
            .build();

        String response = mockMvc.perform(post("/v2/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupPayloadJson(
                    request.getUsername(),
                    request.getPassword(),
                    request.getFullName(),
                    request.getEmail(),
                    request.getSchoolId()
                )))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").isNumber())
            .andExpect(jsonPath("$.username").value(request.getUsername()))
            .andReturn()
            .getResponse()
            .getContentAsString();

        int signupId = objectMapper.readTree(response).get("userId").asInt();
        assertTrue(signupUserJpaRepository.findById(signupId).isPresent());
    }

    @Test
    @WithMockUser(username = "admin")
    void approveSignupEndpointSuccess() throws Exception {
        CreatedSignup signup = createSignupThroughApi("approve");

        mockMvc.perform(post("/v2/signup/signup/{id}/approve", signup.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup.request())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Signup approved successfully"));
    }

    @Test
    @WithMockUser(username = "admin")
    void declineSignupEndpointSuccess() throws Exception {
        CreatedSignup signup = createSignupThroughApi("decline");

        mockMvc.perform(delete("/v2/signup/signup/{id}", signup.id()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Signup declined successfully"));
    }

    @Test
    @WithMockUser(username = "admin")
    void getAllSignupUsersEndpointSuccess() throws Exception {
        CreatedSignup signup = createSignupThroughApi("all");

        mockMvc.perform(get("/v2/signup/signup"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.userId==%s)]", signup.id()).exists());
    }
}

