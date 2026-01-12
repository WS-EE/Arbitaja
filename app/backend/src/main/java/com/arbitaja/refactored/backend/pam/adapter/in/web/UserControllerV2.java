package com.arbitaja.refactored.backend.pam.adapter.in.web;

import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.UpdateUserRequest;
import com.arbitaja.refactored.backend.pam.adapter.util.DtoMapper;
import com.arbitaja.refactored.backend.pam.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.GetUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.UpdateUserUseCase;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.UserProfileResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Web adapter (REST Controller) for User operations.
 * This is the inbound adapter in Hexagonal Architecture.
 */
@RestController
@RequestMapping("/api/v2/user")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "User Management v2", description = "User management operations (Hexagonal Architecture)")
public class UserControllerV2 {

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final CreateUserUseCase createUserUseCase;

    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created"),
            @ApiResponse(responseCode = "409", description = "User with username already exists", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = DuplicateEntityException.class)) })
    })
    @PostMapping("/create")
    public ResponseEntity<UserProfileResponse> createUser(@RequestBody SignupRequest request) {
        log.info("Creating user: {}", request);

        CreateUserUseCase.SignupCommand command = CreateUserUseCase.SignupCommand.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .schoolId(request.getSchoolId())
                .build();

        User user = createUserUseCase.createUser(command);

        UserProfileResponse response = DtoMapper.toUserProfileResponse(user);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all users", description = "Retrieve all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = UnauthorizedException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        log.info("Getting all users");
        List<User> users = getUserUseCase.getAllUsers();
        List<UserProfileResponse> responses = users.stream()
                .map(DtoMapper::toUserProfileResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Integer id) {
        log.info("Getting user by id: {}", id);
        User user = getUserUseCase.getUserProfile(id);


        return ResponseEntity.ok(DtoMapper.toUserProfileResponse(user));
    }

    @Operation(summary = "Update user profile", description = "Update user profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "401", description = "Not authorized to update this user", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = UnauthorizedException.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            Authentication authentication) {

        log.info("Updating user: {}", id);

        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("admin"));

        UpdateUserUseCase.UpdateUserCommand command = UpdateUserUseCase.UpdateUserCommand.builder()
                .userId(id)
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .schoolId(request.getSchoolId())
                .build();

        User user = updateUserUseCase.updateUserProfile(
                command,
                authentication.getName(),
                isAdmin
        );

        return ResponseEntity.ok(DtoMapper.toUserProfileResponse(user));
    }

    @Operation(summary = "Delete user", description = "Delete a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Integer id) {
        log.info("Deleting user: {}", id);
        updateUserUseCase.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}

