package com.arbitaja.refactored.backend.pam.adapter.in.web;

import com.arbitaja.refactored.backend.pam.adapter.in.web.annotations.RequiresPermission;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.GeneralMessageResponse;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.SignupResponse;
import com.arbitaja.refactored.backend.pam.adapter.util.SignupUserMapper;
import com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode;
import com.arbitaja.refactored.backend.pam.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.arbitaja.refactored.backend.pam.core.domain.enums.PermissionCode.ACCEPT_SIGNUPS;

@RestController
@RequestMapping("/v2/signup")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Signup Management v2", description = "Signup management operations (Hexagonal Architecture)")
public class SignupControllerV2 {

    private final CreateUserUseCase createUserUseCase;
    private final SignupUserMapper signupUserMapper;

    @SuppressWarnings("JvmTaintAnalysis")
    @Operation(summary = "Signup user", description = "Create a signup request for a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Signup request created"),
        @ApiResponse(responseCode = "409", description = "User with username already exists", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = DuplicateEntityException.class))})
    })
    @PostMapping()
    public ResponseEntity<SignupResponse> signupUser(@Valid @RequestBody SignupRequest request) {
        log.info("Processing signup request for: {}", request.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                signupUserMapper.toSignupResponse(
                    createUserUseCase.signupUser(
                        signupUserMapper.toSignupCommand(request))
                ));
    }

    @Operation(summary = "Approve signup", description = "Approve a pending signup request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signup approved"),
        @ApiResponse(responseCode = "404", description = "Signup request not found", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = EntityNotFoundException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("/signup/{id}/approve")
    @RequiresPermission(ACCEPT_SIGNUPS)
    public ResponseEntity<GeneralMessageResponse> approveSignup(@PathVariable Integer id, @RequestBody @Valid SignupRequest request) {
        log.info("Approving signup: {}", id);

        createUserUseCase.approveSignupUser(signupUserMapper.toApproveSignupCommand(id, request));

        return ResponseEntity.ok(new GeneralMessageResponse("Signup approved successfully"));
    }

    @Operation(summary = "Decline signup", description = "Decline/reject a pending signup request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Signup declined"),
        @ApiResponse(responseCode = "404", description = "Signup request not found", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = EntityNotFoundException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @DeleteMapping("/signup/{id}")
    @RequiresPermission(ACCEPT_SIGNUPS)
    public ResponseEntity<GeneralMessageResponse> declineSignup(@PathVariable Integer id) {
        log.info("Declining signup: {}", id);
        createUserUseCase.declineSignupUser(id);
        return ResponseEntity.ok(new GeneralMessageResponse("Signup declined successfully"));
    }


    @Operation(summary = "Get all signup users", description = "Retrieve all signup requests in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved signup users"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema =
        @Schema(implementation = UnauthorizedException.class))})
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/signup")
    @RequiresPermission(PermissionCode.VIEW_SIGNUPS)
    public ResponseEntity<List<SignupResponse>> getAllSignupUsers() {
        log.info("Get All Signup Users");
        return ResponseEntity.ok(
            createUserUseCase.getAllSignupUsers()
                .stream()
                .map(signupUserMapper::toSignupResponse)
                .toList());
    }
}
