package com.arbitaja.refactored.backend.pam.adapter.in.web;

import static com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode.*;

import com.arbitaja.refactored.backend.pam.adapter.in.web.annotations.RequiresPermission;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request.AddPermissionToRoleRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request.CreateRoleRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.RoleResponse;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.port.in.role.CreateRoleUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.role.GetRoleUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.role.ManageRolePermissionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Web adapter (REST Controller) for Role operations.
 */
@RestController
@RequestMapping("/v2/roles")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Role Management v2", description = "Role management operations (Hexagonal Architecture)")
public class RoleControllerV2 {

    private final GetRoleUseCase getRoleUseCase;
    private final CreateRoleUseCase createRoleUseCase;
    private final ManageRolePermissionsUseCase manageRolePermissionsUseCase;

    @Operation(summary = "Get all roles", description = "Retrieve all roles in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved roles"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = UnauthorizedException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping
    @RequiresPermission(VIEW_ROLES)
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        log.info("Getting all roles");
        List<RoleResponse> roles = getRoleUseCase.getAllRoles().stream()
                .map(this::toRoleResponse)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Get role by ID", description = "Retrieve a specific role by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved role"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/{id}")
    @RequiresPermission(VIEW_ROLES)
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Integer id) {
        log.info("Getting role by id: {}", id);
        return getRoleUseCase.getRoleById(id)
                .map(this::toRoleResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get role by name", description = "Retrieve a specific role by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved role"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/name/{name}")
    @RequiresPermission(VIEW_ROLES)
    public ResponseEntity<RoleResponse> getRoleByName(@PathVariable String name) {
        log.info("Getting role by name: {}", name);
        return getRoleUseCase.getRoleByName(name)
                .map(this::toRoleResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user roles", description = "Retrieve roles for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved roles")
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoleResponse>> getRolesByUserId(@PathVariable Integer userId) {
        log.info("Getting roles for user: {}", userId);
        List<RoleResponse> roles = getRoleUseCase.getRolesByUserId(userId).stream()
                .map(this::toRoleResponse)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Create new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created role"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = UnauthorizedException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("/create")
    @RequiresPermission(CREATE_UPDATE_ROLES)
    public ResponseEntity<RoleResponse> createRole(@RequestBody CreateRoleRequest request) {
        log.info("Creating new role: {}", request.name());
        return ResponseEntity.status(201).body(
                toRoleResponse(
                    createRoleUseCase.createRole(
                        new CreateRoleUseCase.RoleCommand(request.name(), request.permissionIds())
                    )
                )
        );
    }

    @Operation(summary = "Update role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated role"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{id}")
    @RequiresPermission(CREATE_UPDATE_ROLES)
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Integer id, @RequestBody CreateRoleRequest request) {
        log.info("Updating role: {}", id);
        return ResponseEntity.ok(toRoleResponse(
                createRoleUseCase.updateRole(id, new CreateRoleUseCase.RoleCommand(request.name(), request.permissionIds()))
        ));
    }

    @Operation(summary = "Overwrite role permissions", description = "Overwrite all permissions for a specific role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated role permissions"),
            @ApiResponse(responseCode = "404", description = "Role or permission not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{roleId}/permissions")
    @RequiresPermission({CREATE_UPDATE_ROLES})
    public ResponseEntity<RoleResponse> overwriteRolePermissions(
            @PathVariable Integer roleId,
            @RequestBody AddPermissionToRoleRequest request) {
        log.info("Overwriting permissions {} for role {}", request.permissionIds(), roleId);
        return ResponseEntity.ok(toRoleResponse(
                manageRolePermissionsUseCase.overwriteRolePermissions(roleId, request.permissionIds())
        ));
    }


    private RoleResponse toRoleResponse(Role role) {
        List<PermissionCode> permissions = role.getRolePermissions() == null
                ? List.of()
                : role.getRolePermissions().stream()
                        .map(rolePermission -> rolePermission.getPermission().getKey())
                        .map(this::toPermissionCodeOrNull)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        return new RoleResponse(
                role.getId(),
                role.getName(),
                permissions
        );
    }

    private PermissionCode toPermissionCodeOrNull(String permissionKey) {
        try {
            return PermissionCode.valueOf(permissionKey);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
