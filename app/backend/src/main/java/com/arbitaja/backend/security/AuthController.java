package com.arbitaja.backend.security;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.users.APIs.UserService;
import com.arbitaja.backend.users.dataobjects.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;


@EnableMethodSecurity
@RestController
public class AuthController {
    private static final Logger log = LogManager.getLogger(AuthController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public ResponseEntity<?> loginPage(@RequestParam(value = "error", required = false) boolean error, @RequestParam(value = "logout", required = false) boolean logout) throws JsonProcessingException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!error && auth != null) {
            User user = userService.getUserByUsername(auth.getName());
            if(user != null) {
                Personal_data personalData = user.getPersonal_data();
                ResponseEntity<Map<String, ?>> response = userService.mapPersonalData(personalData, user);
                log.info("Sending response for successful login: " + "{}", objectMapper.writeValueAsString(response));
                return response;
            }
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
}
