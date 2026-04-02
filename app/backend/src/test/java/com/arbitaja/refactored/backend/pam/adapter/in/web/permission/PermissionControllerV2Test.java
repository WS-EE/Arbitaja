package com.arbitaja.refactored.backend.pam.adapter.in.web.permission;

import com.arbitaja.refactored.backend.pam.adapter.in.web.PamExceptionHandler;
import com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.request.CreatePermissionRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.response.PermissionResponse;
import com.arbitaja.refactored.backend.pam.adapter.util.PermissionMapper;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CreatePermissionUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.GetPermissionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PermissionControllerV2Test {

    @Mock
    private GetPermissionUseCase getPermissionUseCase;

    @Mock
    private CreatePermissionUseCase createPermissionUseCase;

    @Mock
    private PermissionMapper permissionMapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        PermissionControllerV2 controller = new PermissionControllerV2(
            getPermissionUseCase,
            createPermissionUseCase,
            permissionMapper
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new PamExceptionHandler())
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void createPermissionReturnsMappedResponse() throws Exception {
        CreatePermissionRequest request = new CreatePermissionRequest("View users", "VIEW_USERS");
        Permission domainPermission = Permission.builder().id(5).name("View users").key("VIEW_USERS").build();
        PermissionResponse response = new PermissionResponse(5, "View users", "VIEW_USERS");
        CreatePermissionUseCase.PermissionCommand command =
            new CreatePermissionUseCase.PermissionCommand("View users", "VIEW_USERS");

        when(permissionMapper.toPermissionCommand(request)).thenReturn(command);
        when(createPermissionUseCase.createPermission(command)).thenReturn(domainPermission);
        when(permissionMapper.toPermissionResponse(domainPermission)).thenReturn(response);

        mockMvc.perform(post("/v2/permissions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.name").value("View users"))
            .andExpect(jsonPath("$.key").value("VIEW_USERS"));
    }

    @Test
    void getPermissionByIdReturns404WhenPermissionMissing() throws Exception {
        when(getPermissionUseCase.getPermissionById(12)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v2/permissions/12"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updatePermissionMapsEntityNotFoundTo404() throws Exception {
        CreatePermissionRequest request = new CreatePermissionRequest("Missing", "MISSING");
        CreatePermissionUseCase.PermissionCommand command =
            new CreatePermissionUseCase.PermissionCommand("Missing", "MISSING");

        when(permissionMapper.toPermissionCommand(request)).thenReturn(command);
        when(createPermissionUseCase.updatePermission(99, command))
            .thenThrow(new EntityNotFoundException("Permission not found with id: 99"));

        mockMvc.perform(put("/v2/permissions/update/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Object not found"));
    }
}

