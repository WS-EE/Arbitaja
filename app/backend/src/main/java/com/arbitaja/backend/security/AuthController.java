package com.arbitaja.backend.security;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.users.APIs.UserController;
import com.arbitaja.backend.users.APIs.UserService;
import com.arbitaja.backend.users.APIs.responses.UserProfileResponse;
import com.arbitaja.backend.users.dataobjects.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@EnableMethodSecurity
@RestController
public class AuthController {
    private static final Logger log = LogManager.getLogger(AuthController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    @Operation(
            summary = "login/logout api",
            description = "On successful login returns a structured JSON object containing user profile, roles, permissions, and associated school data. On successful logout returns the user logged out",
            security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful user login",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "204", description = "Successfully user logout",
                    content = @Content(schema = @Schema(example = "user with username: {username} logged out."))),
            @ApiResponse(responseCode = "400", description = "Invalid username or password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserController.ErrorResponse.class))),
    })
    public ResponseEntity<?> loginPage(@RequestParam(value = "error", required = false) boolean error, @RequestParam(value = "logout", required = false) boolean logout) throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!error && auth != null) {
            User user = userService.getUserByUsername(auth.getName());
            if(user != null) {
                Personal_data personalData = user.getPersonal_data();
                ResponseEntity<UserProfileResponse> response = userService.mapPersonalData(personalData, user);
                log.info("Sending response for successful login: " + "{}", objectMapper.writeValueAsString(response));
                return response;
            }
        }

        if(logout && auth != null) {
            ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NO_CONTENT).body("User with username: " + auth.getName() + " logged out.");
            log.info("Sending response for successful logout: " + "{}", objectMapper.writeValueAsString(response));
            return response;
        }
        ResponseEntity<UserController.ErrorResponse> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserController.ErrorResponse("Invalid username or password"));
        log.info("Sending Response for unsuccessful login: " + "{}", objectMapper.writeValueAsString(response));
        return response;
    }
}
