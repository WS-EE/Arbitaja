package com.arbitaja.backend.users.APIs;
import com.arbitaja.backend.users.APIs.responses.UserProfileResponse;
import com.arbitaja.backend.users.dataobjects.SignupUser;
import com.arbitaja.backend.users.dataobjects.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


import java.util.List;
import java.util.Map;


@EnableMethodSecurity
@RestController("user")
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LogManager.getLogger(UserController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    UserService userService;

    @GetMapping("/profile/get")
    @PreAuthorize("hasAuthority('basic')")
    @Operation(
            summary = "Get user profile details",
            description = "Returns a structured JSON object containing user profile, roles, permissions, and associated school data. If user is an admin and id is included can request other users profile",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "User is not an admin",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"unauthorized\"}")})),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"User not found\"}")})),
    })
    public ResponseEntity<?> getUserProfile(@RequestParam(required = false) Integer id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user;
        if(id != null && auth != null){
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            user = userService.getUserById(id);
        }
        else {
            if(auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("unauthorized"));
            user = userService.getUserByUsername(auth.getName());
        }
        if (user != null) {
            UserProfileResponse userProfileResponse = userService.mapPersonalData(user.getPersonal_data(), user);
            return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not Found"));
    }

    @GetMapping("/profile/all")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Get all user profile details",
            description = "Returns all user profiles",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profiles",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserProfileResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<?> getAllUserProfile(){
        try{
             ResponseEntity<?> resp = userService.getAllUsers();
             log.debug("Sending Response for all users: " + "{}", objectMapper.writeValueAsString(resp));
             return resp;
        }catch (Exception e) {
            log.error("Failed to update user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @Transactional
    @DeleteMapping("/profile/delete")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Delete user by id",
            description = "Deletes user",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"message\": \"User was deleted successfully\"}")})),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"error\": \"User not found\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<?> deleteUserProfile(@RequestParam Integer id){
        try{
            ResponseEntity<?> resp = userService.deleteUser(id);
            log.debug("Sending Response for delete user profile: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error("Failed to delete user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred"));
        }
    }

    @Transactional
    @PutMapping("/profile/edit")
    @PreAuthorize("hasAuthority('basic')")
    @Operation(
            summary = "Update user profile",
            description = "Returns a structured JSON object containing user profile, roles, permissions, and associated school data.",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(value = "{\"error\": \"User not found\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<?> updateUserProfile(@RequestBody User sentUser) {
        try {
            ResponseEntity<?> resp = userService.updatePersonalData(SecurityContextHolder.getContext().getAuthentication(), sentUser);
            log.debug("Sending Response for updated user: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e) {
            log.error("Failed to update user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @Transactional
    @PostMapping("/signup/create")
    @Operation(
            summary = "Create signup user",
            description = "Returns if the signup user creation was successful",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user profile",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"message\": \"User created successfully\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<?> createSignupUser(@RequestBody SignupUser signupUser){
        try{
            ResponseEntity<Map<String, ?>> resp = userService.signupUser(signupUser);
            log.debug("Sending Response for SignupUser: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @Transactional
    @PostMapping("/signup/approve")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Approve signup user",
            description = "Returns if signup user approval and user creation was successful",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully approved signup user",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"message\": \"User approved successfully\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<?> approveSignupUser(@RequestBody SignupUser signupUser){
        try{
            ResponseEntity<Map<String, ?>> resp = userService.approveUser(signupUser);
            log.debug("Sending Response for user confirmation: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        }catch (Exception e){
            log.error("An error occurred while confirming user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @Transactional
    @DeleteMapping("/signup/approve")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Decline signup user",
            description = "Returns if declining signup user its deletion was successful",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signup user deletion was successful",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = "{\"message\": \"User declined successfully\"}")})),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<?> deleteSignupUser(@RequestBody SignupUser signupUser){
        try{
            ResponseEntity<Map<String, ?>> resp = userService.declineUser(signupUser);
            log.debug("Sending Response for declining user: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e){
            log.error("An error occurred while confirming user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }


    @GetMapping("/signup/get")
    @PreAuthorize("hasAuthority('admin')")
    @Operation(
            summary = "Get all signup requests (signup users)",
            description = "Returns a JSON list of signup users",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signup user deletion was successful",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SignupUser.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<?> getSignups(){
        try{
            List<SignupUser> signupUsers = userService.signupUserList();
            ResponseEntity<Map<String, ?>> resp = ResponseEntity.ok(Map.of("signup_users", signupUsers));
            log.debug("Sending Response for Signups: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : "An error occurred"));
        }
    }

    @Transactional
    @PutMapping("/profile/update_password")
    @PreAuthorize("hasAuthority('basic')")
    public ResponseEntity<?> updatePassword(@RequestBody User user){
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ResponseEntity<?> resp = userService.changePassword(user, auth);
            log.debug("Sending Response for successful password change: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred"));
        }
    }


    public static class ErrorResponse {
        @Schema(description = "error message", example = "An error occurred")
        String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}