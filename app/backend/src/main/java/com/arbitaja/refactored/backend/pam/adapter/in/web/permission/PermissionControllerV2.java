package com.arbitaja.refactored.backend.pam.adapter.in.web.permission;

import com.arbitaja.refactored.backend.pam.adapter.in.web.annotations.RequiresPermission;
import com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.request.CreatePermissionRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.response.PermissionResponse;
import com.arbitaja.refactored.backend.pam.adapter.util.PermissionMapper;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CreatePermissionUseCase;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode.*;

/**
 * Web adapter (REST Controller) for Permission operations.
 */
@RestController
@RequestMapping("/v2/permissions")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Permission Management v2", description = "Permission management operations (Hexagonal Architecture)")
@ConditionalOnProperty(name = "arbitaja.pam.mode", havingValue = "hex")
public class PermissionControllerV2 {

    private final GetPermissionUseCase getPermissionUseCase;
    private final CreatePermissionUseCase createPermissionUseCase;
    private final PermissionMapper permissionMapper;

    @Operation(summary = "Get all permissions", description = "Retrieve all permissions in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved permissions"),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = ForbiddenException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping
    @RequiresPermission(VIEW_PERMISSIONS)
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        log.info("Getting all permissions");


        return ResponseEntity.ok(getPermissionUseCase.getAllPermissions()
            .stream()
            .map(permissionMapper::toPermissionResponse)
            .toList());
    }

    @Operation(summary = "Get permission by ID", description = "Retrieve a specific permission by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved permission"),
        @ApiResponse(responseCode = "404", description = "Permission not found", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = EntityNotFoundException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/{id}")
    @RequiresPermission(VIEW_PERMISSIONS)
    public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable Integer id) {
        log.info("Getting permission by id: {}", id);

        return getPermissionUseCase.getPermissionById(id)
            .map(permissionMapper::toPermissionResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user permissions", description = "Retrieve permissions for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved permissions")
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/user/{userId}")
    @RequiresPermission({VIEW_PERMISSIONS, VIEW_USERS})
    public ResponseEntity<List<PermissionResponse>> getPermissionsByUserId(@PathVariable Integer userId) {
        log.info("Getting permissions for user: {}", userId);
        return ResponseEntity.ok(getPermissionUseCase.getPermissionsByUserId(userId)
            .stream()
            .map(permissionMapper::toPermissionResponse)
            .toList());
    }


    @Operation(summary = "Create new permission")
    @PostMapping("/create")
    @RequiresPermission(CREATE_UPDATE_PERMISSIONS)
    public ResponseEntity<PermissionResponse> createPermission(@RequestBody CreatePermissionRequest request) {
        return ResponseEntity.ok(
            permissionMapper.toPermissionResponse(
                createPermissionUseCase.createPermission(
                    permissionMapper.toPermissionCommand(request)
                )
            )
        );
    }

    @PutMapping("/update/{id}")
    @RequiresPermission(CREATE_UPDATE_PERMISSIONS)
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable Integer id, @RequestBody CreatePermissionRequest request) {
        return ResponseEntity.ok(permissionMapper.toPermissionResponse(
            createPermissionUseCase.updatePermission(id, permissionMapper.toPermissionCommand(request))
        ));
    }
}


