package com.arbitaja.refactored.backend.pam.adapter.in.web.user;

import com.arbitaja.refactored.backend.pam.adapter.in.web.PamExceptionHandler;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.OverwriteUserRolesRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.UpdateUserRequest;
import com.arbitaja.refactored.backend.pam.adapter.util.SignupUserMapper;
import com.arbitaja.refactored.backend.pam.adapter.util.UserMapper;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.GetUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.ManageUserRolesUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.UpdateUserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerV2IT {

    @Mock
    private GetUserUseCase getUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private ManageUserRolesUseCase manageUserRolesUseCase;

    @Mock
    private SignupUserMapper signupUserMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CheckPermissionUseCase checkPermissionUseCase;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UserControllerV2 controller = new UserControllerV2(
            getUserUseCase,
            updateUserUseCase,
            createUserUseCase,
            manageUserRolesUseCase,
            signupUserMapper,
            userMapper,
            checkPermissionUseCase
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new PamExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUserReturnsMappedProfile() throws Exception {
        SignupRequest request = SignupRequest.builder()
            .username("alice")
            .password("pass")
            .fullName("Alice")
            .email("alice@example.com")
            .schoolId(1)
            .build();
        CreateUserUseCase.SignupCommand command = CreateUserUseCase.SignupCommand.builder()
            .username("alice")
            .password("pass")
            .fullName("Alice")
            .email("alice@example.com")
            .schoolId(1)
            .build();

        when(signupUserMapper.toSignupCommand(any(SignupRequest.class))).thenReturn(command);
        when(createUserUseCase.createUser(command)).thenReturn(user(5, "alice"));

        mockMvc.perform(post("/v2/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void getAllUsersReturnsMappedCollection() throws Exception {
        when(getUserUseCase.getAllUsers()).thenReturn(List.of(user(1, "a"), user(2, "b")));

        mockMvc.perform(get("/v2/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].username").value("b"));
    }

    @Test
    void getUserByIdReturnsMappedUser() throws Exception {
        when(getUserUseCase.getUserProfile(8)).thenReturn(user(8, "lookup"));

        mockMvc.perform(get("/v2/user/8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(8))
            .andExpect(jsonPath("$.personal_data.full_name").value("lookup name"));
    }

    @Test
    void updateUserReturnsUpdatedProfile() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
            .username("edited")
            .fullName("Edited Name")
            .email("edited@example.com")
            .schoolId(3)
            .build();
        UpdateUserUseCase.UpdateUserCommand command = UpdateUserUseCase.UpdateUserCommand.builder()
            .userId(6)
            .username("edited")
            .fullName("Edited Name")
            .email("edited@example.com")
            .schoolId(3)
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken("admin", "n/a");

        when(checkPermissionUseCase.assertUserHasPermissions(org.mockito.ArgumentMatchers.eq("admin"), any(PermissionCode[].class)))
            .thenReturn(true);
        when(userMapper.toUpdateUserCommand(eq(6), any(UpdateUserRequest.class))).thenReturn(command);
        when(updateUserUseCase.updateUserProfile(command, "admin", true)).thenReturn(user(6, "edited"));

        mockMvc.perform(put("/v2/user/6")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(6))
            .andExpect(jsonPath("$.username").value("edited"));
    }

    @Test
    void updateUserMapsUnauthorizedTo401() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
            .username("blocked")
            .fullName("Blocked")
            .email("blocked@example.com")
            .build();
        UpdateUserUseCase.UpdateUserCommand command = UpdateUserUseCase.UpdateUserCommand.builder()
            .userId(3)
            .username("blocked")
            .fullName("Blocked")
            .email("blocked@example.com")
            .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken("viewer", "n/a");

        when(checkPermissionUseCase.assertUserHasPermissions(org.mockito.ArgumentMatchers.eq("viewer"), any(PermissionCode[].class)))
            .thenReturn(false);
        when(userMapper.toUpdateUserCommand(eq(3), any(UpdateUserRequest.class))).thenReturn(command);
        when(updateUserUseCase.updateUserProfile(command, "viewer", false))
            .thenThrow(UnauthorizedException.notAuthorizedToModifyUser());

        mockMvc.perform(put("/v2/user/3")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void overwriteUserRolesReturnsUpdatedUser() throws Exception {
        when(manageUserRolesUseCase.overwriteUserRoles(9, List.of(1, 2))).thenReturn(user(9, "roles-user"));

        mockMvc.perform(put("/v2/user/9/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OverwriteUserRolesRequest(List.of(1, 2)))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(9));
    }

    @Test
    void deleteUserReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/v2/user/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(updateUserUseCase).deleteUser(10);
    }

    @Test
    void getUserAuthReturnsAuthenticatedUserProfile() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("active", "n/a"));
        when(getUserUseCase.getUserByUsername("active")).thenReturn(Optional.of(user(12, "active")));

        mockMvc.perform(get("/v2/user/auth"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.username").value("active"));
    }

    private User user(int id, String username) {
        return User.builder()
            .id(id)
            .username(username)
            .saltedPassword("hash")
            .personalData(PersonalData.builder()
                .id(id)
                .fullName(username + " name")
                .email(username + "@example.com")
                .school(School.builder().id(1).name("School").build())
                .build())
            .build();
    }
}


