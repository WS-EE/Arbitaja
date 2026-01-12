package com.arbitaja.refactored.backend.pam.adapter.in.web;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.GetPermissionUseCase;
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
 * Web adapter (REST Controller) for Permission operations.
 */
@RestController
@RequestMapping("/api/v2/permissions")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Permission Management v2", description = "Permission management operations (Hexagonal Architecture)")
public class PermissionControllerV2 {

    private final GetPermissionUseCase getPermissionUseCase;

    @Operation(summary = "Get all permissions", description = "Retrieve all permissions in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved permissions"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = UnauthorizedException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        log.info("Getting all permissions");
        List<Permission> permissions = getPermissionUseCase.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Get permission by ID", description = "Retrieve a specific permission by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved permission"),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Integer id) {
        log.info("Getting permission by id: {}", id);
        return getPermissionUseCase.getPermissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user permissions", description = "Retrieve permissions for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved permissions")
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Permission>> getPermissionsByUserId(@PathVariable Integer userId) {
        log.info("Getting permissions for user: {}", userId);
        List<Permission> permissions = getPermissionUseCase.getPermissionsByUserId(userId);
        return ResponseEntity.ok(permissions);
    }
}

