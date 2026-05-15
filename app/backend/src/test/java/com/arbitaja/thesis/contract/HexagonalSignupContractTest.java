package com.arbitaja.thesis.contract;

import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.SignupControllerV2;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.response.SignupResponse;
import com.arbitaja.refactored.backend.pam.adapter.util.SignupUserMapper;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
class HexagonalSignupContractTest extends AbstractSignupContractTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private SignupUserMapper signupUserMapper;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SignupControllerV2 controller = new SignupControllerV2(createUserUseCase, signupUserMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Override
    protected SignupContractClient client() {
        return () -> {
            SignupUser bob = signupUser(2, "bob");
            SignupUser alice = signupUser(1, "alice");
            Pageable pageable = PageRequest.of(0, 20);

            when(createUserUseCase.getAllSignupUsersPaged(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(bob, alice), pageable, 2));
            when(signupUserMapper.toSignupResponse(bob)).thenReturn(signupResponse(2L, "bob"));
            when(signupUserMapper.toSignupResponse(alice)).thenReturn(signupResponse(1L, "alice"));

            MvcResult result = mockMvc.perform(get("/v2/signup/signup"))
                .andExpect(status().isOk())
                .andReturn();

            JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
            return StreamSupport.stream(root.get("content").spliterator(), false)
                .map(node -> node.path("username").asText())
                .sorted(Comparator.naturalOrder())
                .toList();
        };
    }

    private SignupUser signupUser(int id, String username) {
        return SignupUser.builder()
            .id(id)
            .username(username)
            .saltedPassword("ignored")
            .personalData(PersonalData.builder().fullName(username).email(username + "@example.com").build())
            .build();
    }

    private SignupResponse signupResponse(long id, String username) {
        return SignupResponse.builder()
            .userId(id)
            .username(username)
            .email(username + "@example.com")
            .schoolId(null)
            .build();
    }
}

