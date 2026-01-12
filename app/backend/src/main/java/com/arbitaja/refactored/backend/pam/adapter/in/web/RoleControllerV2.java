package com.arbitaja.refactored.backend.pam.adapter.in.web;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.port.in.role.GetRoleUseCase;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Web adapter (REST Controller) for Role operations.
 */
@RestController
@RequestMapping("/api/v2/roles")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Role Management v2", description = "Role management operations (Hexagonal Architecture)")
public class RoleControllerV2 {

    private final GetRoleUseCase getRoleUseCase;

    @Operation(summary = "Get all roles", description = "Retrieve all roles in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved roles"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = UnauthorizedException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<Role>> getAllRoles() {
        log.info("Getting all roles");
        List<Role> roles = getRoleUseCase.getAllRoles();
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
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id) {
        log.info("Getting role by id: {}", id);
        return getRoleUseCase.getRoleById(id)
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
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
        log.info("Getting role by name: {}", name);
        return getRoleUseCase.getRoleByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user roles", description = "Retrieve roles for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved roles")
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Role>> getRolesByUserId(@PathVariable Integer userId) {
        log.info("Getting roles for user: {}", userId);
        List<Role> roles = getRoleUseCase.getRolesByUserId(userId);
        return ResponseEntity.ok(roles);
    }
}

