package com.arbitaja.refactored.backend.pam.adapter.in.web;

import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.GeneralMessageResponse;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.SignupResponse;
import com.arbitaja.refactored.backend.pam.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.UnauthorizedException;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/signup")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Signup Management v2", description = "Signup management operations (Hexagonal Architecture)")
public class SignupControllerV2 {

    private final CreateUserUseCase createUserUseCase;

    @Operation(summary = "Signup user", description = "Create a signup request for a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Signup request created"),
            @ApiResponse(responseCode = "409", description = "User with username already exists", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = DuplicateEntityException.class)) })
    })
    @PostMapping()
    public ResponseEntity<SignupResponse> signupUser(@RequestBody SignupRequest request) {
        log.info("Processing signup request for: {}", request.getUsername());

        CreateUserUseCase.SignupCommand command = CreateUserUseCase.SignupCommand.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .schoolId(request.getSchoolId())
                .build();

        SignupUser signupUser = createUserUseCase.signupUser(command);
        SignupResponse response = SignupResponse.builder()
                .userId(signupUser.getId())
                .username(signupUser.getUsername())
                .email(signupUser.getPersonalData().getEmail())
                .schoolId(signupUser.getPersonalData().getId())
                .message("Signup request created successfully")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Approve signup", description = "Approve a pending signup request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signup approved"),
            @ApiResponse(responseCode = "404", description = "Signup request not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("/signup/{id}/approve")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<GeneralMessageResponse> approveSignup(@PathVariable Integer id) {
        log.info("Approving signup: {}", id);

        CreateUserUseCase.ApproveSignupCommand command = CreateUserUseCase.ApproveSignupCommand.builder()
                .signupUserId(id)
                .build();

        createUserUseCase.approveSignupUser(command);

        return ResponseEntity.ok(new GeneralMessageResponse("Signup approved successfully"));
    }

    @Operation(summary = "Decline signup", description = "Decline/reject a pending signup request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signup declined"),
            @ApiResponse(responseCode = "404", description = "Signup request not found", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = EntityNotFoundException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @DeleteMapping("/signup/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<GeneralMessageResponse> declineSignup(@PathVariable Integer id) {
        log.info("Declining signup: {}", id);
        createUserUseCase.declineSignupUser(id);
        return ResponseEntity.ok(new GeneralMessageResponse("Signup declined successfully"));
    }


    @Operation(summary = "Get all signup users", description = "Retrieve all signup requests in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved signup users"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(mediaType = "application/json", schema =
            @Schema(implementation = UnauthorizedException.class)) })
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/signup")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<SignupResponse>> getAllSignupUsers() {
        log.info("Get All Signup Users");
        List<SignupUser> allSignupUsers = createUserUseCase.getAllSignupUsers();

        List<SignupResponse> response = allSignupUsers.stream().map(su -> SignupResponse.builder()
                .userId(su.getId())
                .username(su.getUsername())
                .email(su.getPersonalData().getEmail())
                .schoolId(su.getPersonalData().getSchool() != null ? su.getPersonalData().getSchool().getId() : null)
                .message("Signup request retrieved successfully")
                .build()).toList();

        return ResponseEntity.ok(response);
    }
}
