package com.arbitaja.backend.users;
import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.users.dataobjects.User;
import com.arbitaja.backend.users.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@EnableMethodSecurity
@RestController
public class UserController {
    private static final Logger log = LogManager.getLogger(UserController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public ResponseEntity<?> loginPage(@RequestParam(value = "error", required = false) boolean error, @RequestParam(value = "logout", required = false) boolean logout) throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (error || auth == null) {
            ResponseEntity<Map<String, Object>> response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
            log.info("Sending Response for unsuccessful login: " + "{}", objectMapper.writeValueAsString(response));
            return response;
        }

        if(logout) {
            ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("User with username: " + auth.getName() + " logged out.");
            log.info("Sending response for successful logout: " + "{}", objectMapper.writeValueAsString(response));
            return response;
        }
        ResponseEntity<Map<String, Object>> response = ResponseEntity.status(HttpStatus.OK).body(Map.of("username", auth.getName(), "roles", auth.getAuthorities()));
        log.info("Sending response for successful login: " + "{}", objectMapper.writeValueAsString(response));
        return response;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('basic')")
    public ResponseEntity<?> getUserProfile() throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findUserByUsername(username);
        Personal_data personalData = user.getPersonal_data();
        ResponseEntity<Map<String, ?>> response = personalData != null ?
                ResponseEntity.status(HttpStatus.OK).body(Map.of("username", username,"roles", auth.getAuthorities(), "personal_data",
                Map.of("full_name", personalData.getFull_name(), "email", personalData.getEmail()),
                        "school", Map.of("id", personalData.getSchool().getId(), "name", personalData.getSchool().getName())))
                :
                ResponseEntity.status(HttpStatus.OK).body(Map.of("username", username,"roles", auth.getAuthorities(),
                "personal_data", Map.of("full_name", "", "email", "",
                        "school", Map.of("id", "", "name", ""))));

        log.debug("Sending Response for authenticated user: " + "{}", objectMapper.writeValueAsString(response));
        return response;
    }
}