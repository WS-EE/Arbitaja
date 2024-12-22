package com.arbitaja.backend.users;
import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.users.dataobjects.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@EnableMethodSecurity
@RestController
public class UserController {
    private static final Logger log = LogManager.getLogger(UserController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    UserService userService;

    @GetMapping("/login")
    public ResponseEntity<?> loginPage(@RequestParam(value = "error", required = false) boolean error, @RequestParam(value = "logout", required = false) boolean logout) throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!error && auth != null) {
            ResponseEntity<Map<String, Object>> response = ResponseEntity.status(HttpStatus.OK).body(Map.of("username", auth.getName(), "roles", auth.getAuthorities()));
            log.info("Sending response for successful login: " + "{}", objectMapper.writeValueAsString(response));
            return response;
        }

        if(logout && auth != null) {
            ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("User with username: " + auth.getName() + " logged out.");
            log.info("Sending response for successful logout: " + "{}", objectMapper.writeValueAsString(response));
            return response;
        }
        ResponseEntity<Map<String, Object>> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid username or password"));
        log.info("Sending Response for unsuccessful login: " + "{}", objectMapper.writeValueAsString(response));
        return response;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('basic')")
    public ResponseEntity<?> getUserProfile() throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.getUserByUsername(username);
        if(user != null) {
            Personal_data personalData = user.getPersonal_data();
            ResponseEntity<Map<String, ?>> response = userService.mapPersonalData(auth, personalData);
            log.debug("Sending Response for authenticated user: " + "{}", objectMapper.writeValueAsString(response));
            return response;
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found");
    }

    @Transactional
    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, ?> userProfile) {
        try {
            ResponseEntity<Map<String, ?>> resp = userService.updatePersonalData(SecurityContextHolder.getContext().getAuthentication(), userProfile);
            log.debug("Sending Response for updated user: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e) {
            log.error("Failed to update user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","An error occurred"));
        }
    }
}