package com.arbitaja.refactored.backend.pam.adapter.in.web.signup;

import com.arbitaja.refactored.backend.pam.adapter.in.web.PamExceptionHandler;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.response.SignupResponse;
import com.arbitaja.refactored.backend.pam.adapter.util.SignupUserMapper;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SignupControllerV2IT {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private SignupUserMapper signupUserMapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        SignupControllerV2 controller = new SignupControllerV2(createUserUseCase, signupUserMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new PamExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void signupUserReturnsCreatedResponse() throws Exception {
        SignupRequest request = signupRequest("new-user", "pass", "New User", "new@example.com", 2);
        CreateUserUseCase.SignupCommand command = signupCommand("new-user", "pass", "New User", "new@example.com", 2);
        SignupUser signupUser = signupUser(15, "new-user", "new@example.com", 2);
        SignupResponse response = new SignupResponse(15L, "new-user", "new@example.com", "New User", 2, null);

        when(signupUserMapper.toSignupCommand(any(SignupRequest.class))).thenReturn(command);
        when(createUserUseCase.signupUser(command)).thenReturn(signupUser);
        when(signupUserMapper.toSignupResponse(signupUser)).thenReturn(response);

        mockMvc.perform(post("/v2/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(15))
            .andExpect(jsonPath("$.username").value("new-user"));
    }

    @Test
    void approveSignupReturnsSuccessMessage() throws Exception {
        SignupRequest request = signupRequest("approved-user", "pass", "Approved User", "approved@example.com", 1);
        CreateUserUseCase.ApproveSignupCommand command = CreateUserUseCase.ApproveSignupCommand.builder()
            .signupUserId(8)
            .username("approved-user")
            .fullName("Approved User")
            .email("approved@example.com")
            .schoolId(1)
            .build();

        when(signupUserMapper.toApproveSignupCommand(eq(8), any(SignupRequest.class))).thenReturn(command);

        mockMvc.perform(post("/v2/signup/signup/8/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Signup approved successfully"));

        verify(createUserUseCase).approveSignupUser(command);
    }

    @Test
    void approveSignupMapsEntityNotFoundTo404() throws Exception {
        SignupRequest request = signupRequest("missing", "pass", "Missing User", "missing@example.com", 1);
        CreateUserUseCase.ApproveSignupCommand command = CreateUserUseCase.ApproveSignupCommand.builder()
            .signupUserId(33)
            .build();

        when(signupUserMapper.toApproveSignupCommand(eq(33), any(SignupRequest.class))).thenReturn(command);
        doThrow(new EntityNotFoundException("SignupUser not found with id: 33"))
            .when(createUserUseCase).approveSignupUser(command);

        mockMvc.perform(post("/v2/signup/signup/33/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Object not found"));
    }

    @Test
    void declineSignupReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/v2/signup/signup/12"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Signup declined successfully"));

        verify(createUserUseCase).declineSignupUser(12);
    }

    @Test
    void getAllSignupUsersReturnsPagedContent() throws Exception {
        SignupUser signupUser1 = signupUser(1, "alpha", "alpha@example.com", 1);
        SignupUser signupUser2 = signupUser(2, "beta", "beta@example.com", 2);
        Pageable pageable = PageRequest.of(0, 20);

        when(createUserUseCase.getAllSignupUsersPaged(eq(""), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(signupUser1, signupUser2), pageable, 2));
        when(signupUserMapper.toSignupResponse(signupUser1))
            .thenReturn(new SignupResponse(1L, "alpha", "alpha@example.com", "Alpha User", 1, null));
        when(signupUserMapper.toSignupResponse(signupUser2))
            .thenReturn(new SignupResponse(2L, "beta", "beta@example.com", "Beta User", 2, null));

        mockMvc.perform(get("/v2/signup/signup"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].username").value("alpha"))
            .andExpect(jsonPath("$.content[1].schoolId").value(2))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getAllSignupUsersPagedWithSearchFiltersResults() throws Exception {
        SignupUser signupUser = signupUser(3, "charlie", "charlie@example.com", 1);
        Pageable pageable = PageRequest.of(0, 20);

        when(createUserUseCase.getAllSignupUsersPaged(eq("charlie"), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(signupUser), pageable, 1));
        when(signupUserMapper.toSignupResponse(signupUser))
            .thenReturn(new SignupResponse(3L, "charlie", "charlie@example.com", "Charlie", 1, null));

        mockMvc.perform(get("/v2/signup/signup").param("search", "charlie"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].username").value("charlie"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    private SignupRequest signupRequest(String username, String password, String fullName, String email, Integer schoolId) {
        return SignupRequest.builder()
            .username(username)
            .password(password)
            .fullName(fullName)
            .email(email)
            .schoolId(schoolId)
            .build();
    }

    private CreateUserUseCase.SignupCommand signupCommand(String username, String password, String fullName, String email, Integer schoolId) {
        return CreateUserUseCase.SignupCommand.builder()
            .username(username)
            .password(password)
            .fullName(fullName)
            .email(email)
            .schoolId(schoolId)
            .build();
    }

    private SignupUser signupUser(int id, String username, String email, int schoolId) {
        return SignupUser.builder()
            .id(id)
            .username(username)
            .saltedPassword("hash")
            .personalData(PersonalData.builder()
                .fullName("Name")
                .email(email)
                .school(School.builder().id(schoolId).name("School").build())
                .build())
            .build();
    }
}

