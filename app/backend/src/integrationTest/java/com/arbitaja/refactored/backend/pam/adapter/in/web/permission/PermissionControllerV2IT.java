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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PermissionControllerV2IT {

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
    void getAllPermissionsReturnsMappedCollection() throws Exception {
        Permission permission1 = Permission.builder().id(1).name("View users").key("VIEW_USERS").build();
        Permission permission2 = Permission.builder().id(2).name("Edit users").key("EDIT_USERS").build();

        when(getPermissionUseCase.getAllPermissions()).thenReturn(List.of(permission1, permission2));
        when(permissionMapper.toPermissionResponse(permission1)).thenReturn(new PermissionResponse(1, "View users", "VIEW_USERS"));
        when(permissionMapper.toPermissionResponse(permission2)).thenReturn(new PermissionResponse(2, "Edit users", "EDIT_USERS"));

        mockMvc.perform(get("/v2/permissions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getPermissionByIdReturnsMappedResponseWhenFound() throws Exception {
        Permission permission = Permission.builder().id(12).name("View permissions").key("VIEW_PERMISSIONS").build();
        when(getPermissionUseCase.getPermissionById(12)).thenReturn(Optional.of(permission));
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(
            new PermissionResponse(12, "View permissions", "VIEW_PERMISSIONS")
        );

        mockMvc.perform(get("/v2/permissions/12"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(12))
            .andExpect(jsonPath("$.key").value("VIEW_PERMISSIONS"));
    }

    @Test
    void getPermissionsByUserIdReturnsMappedCollection() throws Exception {
        Permission permission = Permission.builder().id(21).name("View roles").key("VIEW_ROLES").build();

        when(getPermissionUseCase.getPermissionsByUserId(99)).thenReturn(List.of(permission));
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(
            new PermissionResponse(21, "View roles", "VIEW_ROLES")
        );

        mockMvc.perform(get("/v2/permissions/user/99"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(21));
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

    @Test
    void updatePermissionReturnsMappedResponseWhenPermissionExists() throws Exception {
        CreatePermissionRequest request = new CreatePermissionRequest("Edit permissions", "CREATE_UPDATE_PERMISSIONS");
        CreatePermissionUseCase.PermissionCommand command =
            new CreatePermissionUseCase.PermissionCommand("Edit permissions", "CREATE_UPDATE_PERMISSIONS");
        Permission updated = Permission.builder()
            .id(7)
            .name("Edit permissions")
            .key("CREATE_UPDATE_PERMISSIONS")
            .build();

        when(permissionMapper.toPermissionCommand(request)).thenReturn(command);
        when(createPermissionUseCase.updatePermission(7, command)).thenReturn(updated);
        when(permissionMapper.toPermissionResponse(updated)).thenReturn(
            new PermissionResponse(7, "Edit permissions", "CREATE_UPDATE_PERMISSIONS")
        );

        mockMvc.perform(put("/v2/permissions/update/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.key").value("CREATE_UPDATE_PERMISSIONS"));
    }
}
