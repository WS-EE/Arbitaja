package com.arbitaja.backend;


import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.users.dataobjects.SignupUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserApiTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "admin")
    void testCreateAndDeleteSignupUser() throws Exception {
        Personal_data personal_data = new Personal_data();
        setPersonalData(personal_data);
        String username = "user" + new Random().nextInt(1000);
        SignupUserRequest signupUser = new SignupUserRequest("123", username, personal_data);

        Map<String, Object> postResult = postResult("/user/signup/create", signupUser);
        assertEquals("User created successfully", postResult.get("message"));

        Map<String, Object> getResult = getResult("/user/signup/get");
        List<Map<String, Object>> signupUsers = (List<Map<String, Object>>) getResult.get("signup_users");

        SignupUser foundSignupUser = signupUsers.stream()
                .filter(user -> username.equals(user.get("username")))
                .map(userMap -> objectMapper.convertValue(userMap, SignupUser.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User '" + username + "' not found"));

        assertEquals(username, foundSignupUser.getUsername(), "User with username '" + username + "' should be present in the list");

        Map<String, String> deleteResult = deleteResult("/user/signup/approve", foundSignupUser);
        assertEquals("User declined successfully", deleteResult.get("message"));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void testCreateAndApproveSignupUser() throws Exception {
        Personal_data personal_data = new Personal_data();
        setPersonalData(personal_data);
        String username = "user" + new Random().nextInt(1000);
        SignupUserRequest signupUser = new SignupUserRequest("123", username, personal_data);

        Map<String, Object> postResult = postResult("/user/signup/create", signupUser);
        assertEquals("User created successfully", postResult.get("message"));

        Map<String, Object> getResult = getResult("/user/signup/get");
        List<Map<String, Object>> signupUsers = (List<Map<String, Object>>) getResult.get("signup_users");

        SignupUser foundSignupUser = signupUsers.stream()
                .filter(user -> username.equals(user.get("username")))
                .map(userMap -> objectMapper.convertValue(userMap, SignupUser.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User '" + username + "' not found"));

        assertEquals(username, foundSignupUser.getUsername(), "User with username '" + username + "' should be present in the list");

        Map<String, Object> approveResult = postResult("/user/signup/approve", foundSignupUser);
        assertEquals("User approved successfully", approveResult.get("message"));
    }


    void setPersonalData(Personal_data personal_data) throws Exception {
        personal_data.setFull_name("user");
        personal_data.setEmail("user@email.com");
    }

    private Map<String, Object> getResult(String uri) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, new TypeReference<>() {});
    }

    private Map<String, Object> postResult(String uri, Object object) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .content(objectMapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, new TypeReference<>() {});
    }

    private Map<String, String> deleteResult(String uri, Object object) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                .content(objectMapper.writeValueAsString(object))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, new TypeReference<>() {});
    }

    private static class SignupUserRequest {
        String username, salted_password;
        Personal_data personal_data;

        public SignupUserRequest(String salted_password, String username, Personal_data personal_data) {
            this.salted_password = salted_password;
            this.username = username;
            this.personal_data = personal_data;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSalted_password() {
            return salted_password;
        }

        public void setSalted_password(String salted_password) {
            this.salted_password = salted_password;
        }

        public Personal_data getPersonal_data() {
            return personal_data;
        }

        public void setPersonal_data(Personal_data personal_data) {
            this.personal_data = personal_data;
        }
    }
}
