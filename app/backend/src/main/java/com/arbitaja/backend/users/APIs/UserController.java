package com.arbitaja.backend.users.APIs;
import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.users.dataobjects.SignupUser;
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
    public ResponseEntity<?> getUserProfile() throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Getting user profile for user: {}, {}", auth.getName(), auth.getDetails());
        User user = userService.getUserByUsername(auth.getName());

        if(user != null) {
            Personal_data personalData = user.getPersonal_data();
            ResponseEntity<Map<String, ?>> response = userService.mapPersonalData(auth, personalData, user);
            log.debug("Sending Response for authenticated user: " + "{}", objectMapper.writeValueAsString(response));
            return response;
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found");
    }

    @Transactional
    @PutMapping("/profile/edit")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateUserProfile(@RequestBody User sentUser) {
        try {
            ResponseEntity<Map<String, ?>> resp = userService.updatePersonalData(SecurityContextHolder.getContext().getAuthentication(), sentUser);
            log.debug("Sending Response for updated user: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e) {
            log.error("Failed to update user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","An error occurred"));
        }
    }

    @PostMapping("/signup/create")
    public ResponseEntity<?> createSignupUser(@RequestBody SignupUser signupUser){
        try{
            ResponseEntity<Map<String, ?>> resp = userService.signupUser(signupUser);
            log.debug("Sending Response for SignupUser: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","An error occurred"));
        }
    }

    @PostMapping("/signup/approve")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> approveSignupUser(@RequestBody SignupUser signupUser){
        try{
            ResponseEntity<Map<String, ?>> resp = userService.confirmUser(signupUser.getId());
            log.debug("Sending Response for user confirmation: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        }catch (Exception e){
            log.error("An error occurred while confirming user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","An error occurred"));
        }
    }

    @GetMapping("/signup/get")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getSignups(){
        try{
            ResponseEntity<Map<String, ?>> resp = ResponseEntity.ok(Map.of("signup_users", userService.signupUserList()));
            log.debug("Sending Response for Signups: " + "{}", objectMapper.writeValueAsString(resp));
            return resp;
        } catch (Exception e) {
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","An error occurred"));

        }
    }
}