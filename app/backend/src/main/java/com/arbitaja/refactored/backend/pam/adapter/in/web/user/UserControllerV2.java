package com.arbitaja.refactored.backend.pam.adapter.in.web.user;

import com.arbitaja.refactored.backend.pam.adapter.in.web.annotations.RequiresPermission;
import com.arbitaja.refactored.backend.pam.adapter.in.web.shared.dto.response.GeneralMessageResponse;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.ChangePasswordRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.OverwriteUserRolesRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.UpdateUserRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.response.UserProfileResponse;
import com.arbitaja.refactored.backend.pam.adapter.util.DtoMapper;
import com.arbitaja.refactored.backend.pam.adapter.util.SignupUserMapper;
import com.arbitaja.refactored.backend.pam.adapter.util.UserMapper;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CheckPermissionUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.GetUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.ManageUserRolesUseCase;
import com.arbitaja.refactored.backend.pam.core.port.in.user.UpdateUserUseCase;
import com.arbitaja.refactored.backend.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode.*;

/**
 * Web adapter (REST Controller) for User operations.
 * This is the inbound adapter in Hexagonal Architecture.
 */
@RestController
@RequestMapping("/v2/user")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "User Management v2", description = "User management operations (Hexagonal Architecture)")
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class UserControllerV2 {

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final ManageUserRolesUseCase manageUserRolesUseCase;
    private final SignupUserMapper signupUserMapper;
    private final UserMapper userMapper;
    private final CheckPermissionUseCase checkPermissionUseCase;

    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created"),
        @ApiResponse(responseCode = "409", description = "User with username already exists", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = DuplicateEntityException.class))})
    })
    @PostMapping("/create")
    @SecurityRequirement(name = "basicAuth")
    @RequiresPermission(ACCEPT_SIGNUPS)
    public ResponseEntity<UserProfileResponse> createUser(@RequestBody @Valid SignupRequest request) {
        log.info("Creating user: {}", request);

        return ResponseEntity.ok(
            DtoMapper.toUserProfileResponse(
                createUserUseCase.createUser(
                    signupUserMapper.toSignupCommand(request)
                )
            ));
    }

    @Operation(summary = "Admin direct user creation", description = "Create a new user directly as an admin")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created"),
        @ApiResponse(responseCode = "409", description = "User with username already exists", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = DuplicateEntityException.class))})
    })
    @PostMapping("/admin-create")
    @SecurityRequirement(name = "basicAuth")
    @RequiresPermission(EDIT_USERS)
    public ResponseEntity<UserProfileResponse> adminCreateUser(@RequestBody @Valid SignupRequest request) {
        log.info("Admin creating user: {}", request);

        return ResponseEntity.ok(
            DtoMapper.toUserProfileResponse(
                createUserUseCase.createUser(
                    signupUserMapper.toSignupCommand(request)
                )
            ));
    }

    @Operation(summary = "Get all users", description = "Retrieve all users in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = ForbiddenException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping
    @RequiresPermission(VIEW_USERS)
    public ResponseEntity<PagedResponse<UserProfileResponse>> getAllUsers(
        @RequestParam(required = false, defaultValue = "") String search,
        @PageableDefault(size = 20, sort = "username", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("Getting users paged, search={}", search);
        return ResponseEntity.ok(PagedResponse.from(
            getUserUseCase.getUsersPaged(search, pageable)
                .map(DtoMapper::toUserProfileResponse)
        ));
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
        @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = EntityNotFoundException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/{id}")
    @RequiresPermission(VIEW_USERS)
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Integer id) {
        log.info("Getting user by id: {}", id);
        User user = getUserUseCase.getUserProfile(id);

        return ResponseEntity.ok(DtoMapper.toUserProfileResponse(user));
    }

    @Operation(summary = "Update user profile", description = "Update user profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized to update this user", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = ForbiddenException.class))}),
        @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = EntityNotFoundException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateUser(
        @PathVariable Integer id,
        @RequestBody @Valid UpdateUserRequest request,
        Authentication authentication) {

        log.info("Updating user: {}", id);


        boolean canEditOthers = checkPermissionUseCase.assertUserHasPermissions(
            authentication.getName(),
            new PermissionCode[]{EDIT_USERS}
        );

        User user = updateUserUseCase.updateUserProfile(
            userMapper.toUpdateUserCommand(id, request),
            authentication.getName(),
            canEditOthers
        );

        return ResponseEntity.ok(DtoMapper.toUserProfileResponse(user));
    }

    @Operation(summary = "Overwrite user roles", description = "Overwrite all roles for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user roles"),
        @ApiResponse(responseCode = "404", description = "User or role not found", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = EntityNotFoundException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @PutMapping("/{id}/roles")
    @RequiresPermission(EDIT_USERS)
    public ResponseEntity<UserProfileResponse> overwriteUserRoles(
        @PathVariable Integer id,
        @RequestBody @Valid OverwriteUserRolesRequest request) {
        log.info("Overwriting roles {} for user {}", request.roleIds(), id);

        User user = manageUserRolesUseCase.overwriteUserRoles(id, request.roleIds());
        return ResponseEntity.ok(DtoMapper.toUserProfileResponse(user));
    }

    @Operation(summary = "Delete user", description = "Delete a user from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = EntityNotFoundException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @DeleteMapping("/{id}")
    @RequiresPermission(EDIT_USERS)
    public ResponseEntity<GeneralMessageResponse> deleteUser(@PathVariable Integer id) {
        log.info("Deleting user: {}", id);
        updateUserUseCase.deleteUser(id);
        return ResponseEntity.ok(new GeneralMessageResponse("User deleted successfully"));
    }

    @GetMapping("/auth")
    @Operation(summary = "Get authenticated user profile", description = "Retrieve the profile of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = ForbiddenException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    ResponseEntity<UserProfileResponse> getUserAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Getting authenticated user profile: {}", username);
        User user = getUserUseCase.getUserByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserProfileResponse response = DtoMapper.toUserProfileResponse(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password/{id}")
    @Operation(summary = "Change user password", description = "Change the password of a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "403", description = "Not authorized to change this user's password", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = ForbiddenException.class))}),
        @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = EntityNotFoundException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    ResponseEntity<GeneralMessageResponse> changePassword(
        @NotNull @PathVariable Integer id,
        @RequestBody @Valid ChangePasswordRequest request,
        Authentication authentication) {

        log.info("Changing password for user: {}", id);

        boolean canEditOthers = checkPermissionUseCase.assertUserHasPermissions(
            authentication.getName(),
            new PermissionCode[]{EDIT_USERS}
        );

        if (!canEditOthers && !authentication.getName().equals(getUserUseCase.getUserProfile(id).getUsername())) {
            throw new ForbiddenException("Not authorized to change this user's password");
        }

        updateUserUseCase.changePassword(
            userMapper.toChangePasswordCommand(id, canEditOthers, request.oldPassword(), request.newPassword()),
            authentication.getName()
        );

        return ResponseEntity.ok(new GeneralMessageResponse("Password changed successfully"));
    }
}


