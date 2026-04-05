package com.arbitaja.refactored.backend.pam.adapter.in.web;

import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.PermissionJpaRepository;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.RoleJpaRepository;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.SchoolJpaRepository;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.SignupUserJpaRepository;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class PamWebE2EBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserJpaRepository userJpaRepository;

    @Autowired
    protected RoleJpaRepository roleJpaRepository;

    @Autowired
    protected PermissionJpaRepository permissionJpaRepository;

    @Autowired
    protected SchoolJpaRepository schoolJpaRepository;

    @Autowired
    protected SignupUserJpaRepository signupUserJpaRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected CreatedUser createUserThroughApi(String prefix) throws Exception {
        String username = unique(prefix + "_user");
        String password = "oldPassword123";
        String email = unique(prefix + "_mail") + "@example.com";
        String fullName = "Test " + prefix;

        SignupRequest request = SignupRequest.builder()
            .username(username)
            .password(password)
            .fullName(fullName)
            .email(email)
            .schoolId(firstSchoolId())
            .build();

        MvcResult result = mockMvc.perform(post("/v2/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupPayloadJson(username, password, fullName, email, request.getSchoolId())))
            .andExpect(status().isOk())
            .andReturn();

        int id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asInt();
        return new CreatedUser(id, username, password, request);
    }

    protected CreatedSignup createSignupThroughApi(String prefix) throws Exception {
        String username = unique(prefix + "_signup");
        String password = "signup123";
        String fullName = "Signup " + prefix;
        String email = unique(prefix + "_signup") + "@example.com";

        SignupRequest request = SignupRequest.builder()
            .username(username)
            .password(password)
            .fullName(fullName)
            .email(email)
            .schoolId(firstSchoolId())
            .build();

        MvcResult result = mockMvc.perform(post("/v2/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupPayloadJson(username, password, fullName, email, request.getSchoolId())))
            .andExpect(status().isCreated())
            .andReturn();

        int userId = objectMapper.readTree(result.getResponse().getContentAsString()).get("userId").asInt();
        return new CreatedSignup(userId, request);
    }

    protected Integer firstSchoolId() {
        return schoolJpaRepository.findAll().stream().findFirst().orElseThrow().getId();
    }

    protected Integer permissionIdByKey(String key) {
        return permissionJpaRepository.findAll().stream()
            .filter(permission -> key.equals(permission.getKey()))
            .findFirst()
            .orElseThrow()
            .getId();
    }

    protected String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    protected String uniquePermissionKey(String prefix) {
        String letters = UUID.randomUUID().toString().replaceAll("[^A-Za-z]", "").toUpperCase();
        String suffix = letters.length() >= 8 ? letters.substring(0, 8) : (letters + "PERMKEYX").substring(0, 8);
        return (prefix + "_" + suffix).toUpperCase();
    }

    protected String signupPayloadJson(String username, String password, String fullName, String email, Integer schoolId){
        return objectMapper.createObjectNode()
            .put("username", username)
            .put("password", password)
            .put("full_name", fullName)
            .put("email", email)
            .put("school_id", schoolId)
            .toString();
    }

    protected record CreatedUser(int id, String username, String password, SignupRequest request) {
    }

    protected record CreatedSignup(int id, SignupRequest request) {
    }
}

