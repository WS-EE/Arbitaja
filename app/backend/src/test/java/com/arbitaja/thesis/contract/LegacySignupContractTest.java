package com.arbitaja.thesis.contract;

import com.arbitaja.backend.users.APIs.UserController;
import com.arbitaja.backend.users.APIs.UserService;
import com.arbitaja.backend.users.dataobjects.SignupUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LegacySignupContractTest extends AbstractSignupContractTest {

    @Mock
    private UserService userService;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        UserController controller = new UserController(objectMapper, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Override
    protected SignupContractClient client() {
        return () -> {
            when(userService.signupUserList()).thenReturn(List.of(signupUser(2, "bob"), signupUser(1, "alice")));

            MvcResult result = mockMvc.perform(get("/v1/user/signup/get"))
                .andExpect(status().isOk())
                .andReturn();

            JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
            return StreamSupport.stream(root.path("signup_users").spliterator(), false)
                .map(node -> node.path("username").asText())
                .sorted(Comparator.naturalOrder())
                .toList();
        };
    }

    private SignupUser signupUser(int id, String username) {
        SignupUser signupUser = new SignupUser();
        signupUser.setId(id);
        signupUser.setUsername(username);
        signupUser.setSalted_password("ignored");
        return signupUser;
    }
}

