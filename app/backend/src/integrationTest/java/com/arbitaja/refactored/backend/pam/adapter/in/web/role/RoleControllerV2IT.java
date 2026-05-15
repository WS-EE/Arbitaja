package com.arbitaja.refactored.backend.pam.adapter.in.web.role;

import com.arbitaja.refactored.backend.pam.adapter.in.web.PamExceptionHandler;
import com.arbitaja.refactored.backend.pam.adapter.in.web.role.dto.request.AddPermissionToRoleRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.role.dto.request.CreateRoleRequest;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.RolePermission;
import com.arbitaja.refactored.backend.pam.core.port.in.role.CreateRoleUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.role.GetRoleUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.role.ManageRolePermissionsUseCase;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoleControllerV2IT {

    @Mock
    private GetRoleUseCase getRoleUseCase;

    @Mock
    private CreateRoleUseCase createRoleUseCase;

    @Mock
    private ManageRolePermissionsUseCase manageRolePermissionsUseCase;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RoleControllerV2 controller = new RoleControllerV2(getRoleUseCase, createRoleUseCase, manageRolePermissionsUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new PamExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllRolesReturnsPagedContent() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        when(getRoleUseCase.getRolesPaged(eq(""), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(role(1, "admin", "VIEW_USERS"), role(2, "user", "VIEW_ROLES")), pageable, 2));

        mockMvc.perform(get("/v2/roles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].permissions[0]").value("VIEW_USERS"))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAllRolesPagedWithSearchFiltersResults() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        when(getRoleUseCase.getRolesPaged(eq("admin"), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(role(1, "admin", "VIEW_USERS")), pageable, 1));

        mockMvc.perform(get("/v2/roles").param("search", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("admin"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getRoleByIdReturns404WhenMissing() throws Exception {
        when(getRoleUseCase.getRoleById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v2/roles/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getRoleByNameReturnsMappedResponse() throws Exception {
        when(getRoleUseCase.getRoleByName("admin")).thenReturn(Optional.of(role(4, "admin", "VIEW_USERS", "EDIT_USERS")));

        mockMvc.perform(get("/v2/roles/name/admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4))
            .andExpect(jsonPath("$.permissions[1]").value("EDIT_USERS"));
    }

    @Test
    void getRolesByUserIdReturnsMappedCollection() throws Exception {
        when(getRoleUseCase.getRolesByUserId(5)).thenReturn(List.of(role(3, "teacher", "VIEW_USERS")));

        mockMvc.perform(get("/v2/roles/user/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("teacher"));
    }

    @Test
    void createRoleReturnsCreatedStatusAndBody() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest("judge", List.of(1, 2));
        Role created = role(10, "judge", "VIEW_USERS");

        when(createRoleUseCase.createRole(new CreateRoleUseCase.RoleCommand("judge", List.of(1, 2))))
            .thenReturn(created);

        mockMvc.perform(post("/v2/roles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.name").value("judge"));
    }

    @Test
    void updateRoleMapsEntityNotFoundTo404() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest("missing", List.of());

        when(createRoleUseCase.updateRole(77, new CreateRoleUseCase.RoleCommand("missing", List.of())))
            .thenThrow(new EntityNotFoundException("Role not found with id: 77"));

        mockMvc.perform(put("/v2/roles/77")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Object not found"));
    }

    @Test
    void overwriteRolePermissionsReturnsUpdatedRole() throws Exception {
        AddPermissionToRoleRequest request = new AddPermissionToRoleRequest(List.of(1, 3));
        when(manageRolePermissionsUseCase.overwriteRolePermissions(11, List.of(1, 3)))
            .thenReturn(role(11, "moderator", "VIEW_USERS", "VIEW_PERMISSIONS"));

        mockMvc.perform(put("/v2/roles/11/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(11))
            .andExpect(jsonPath("$.permissions[0]").value("VIEW_USERS"));
    }

    private Role role(int id, String name, String... permissionKeys) {
        Role role = Role.builder().id(id).name(name).build();
        for (int i = 0; i < permissionKeys.length; i++) {
            String permissionKey = permissionKeys[i];
            RolePermission rolePermission = RolePermission.builder()
                .id(i + 1)
                .role(role)
                .permission(Permission.builder().id(i + 1).name(permissionKey).key(permissionKey).build())
                .build();
            role.getRolePermissions().add(rolePermission);
        }
        return role;
    }
}

