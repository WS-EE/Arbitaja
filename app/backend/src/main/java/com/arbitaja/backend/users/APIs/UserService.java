package com.arbitaja.backend.users.APIs;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.PersonalDataRepository;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import com.arbitaja.backend.users.dataobjects.User;
import com.arbitaja.backend.users.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private PersonalDataRepository personalDataRepository;

    public User getUserByUsername(String username){
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    public User getUserById(Map<String, ?> userProfile){
        int userId = getUserId(userProfile);
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }

    public School getSchoolById(int id) {
        log.debug("Getting school with ID: {}", id);
        Optional<School> school = schoolRepository.findById(id);
        return school.orElse(null);
    }

    public void updateUser(User user){
        userRepository.updateUser(user);
    }

    public void updatePersonalData(Personal_data personalData){
        personalDataRepository.updateOrCreatePersonal_data(personalData);
    }

    public ResponseEntity<Map<String, ?>> mapPersonalData(Authentication auth, Personal_data personalData, User user){
        if(personalData == null){
            return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername(),"roles", auth.getAuthorities(),
                    "personal_data", Map.of("full_name", "", "email", "",
                            "school", Map.of("id", "", "name", ""))));
        }
        return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername(),"roles", auth.getAuthorities(), "personal_data",
                Map.of("full_name", personalData.getFull_name(), "email", personalData.getEmail()),
                "school", Map.of("id", personalData.getSchool().getId(), "name", personalData.getSchool().getName())));
    }

    public ResponseEntity<Map<String, ?>> updatePersonalData(Authentication auth, Map<String, ?> userProfile) {
        log.info("Updating user profile: {}", userProfile);
        try {
            User user = getUserById(userProfile);
            if (user == null) {
                return errorResponse(HttpStatus.NOT_FOUND, "User not found");
            }

            String newUsername = (String) userProfile.get("username");
            if (!validateUsername(newUsername)) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Invalid username");
            }
            user.setUsername(newUsername);

            Personal_data personalData = user.getPersonal_data();
            if (personalData == null) {
                personalData = new Personal_data();
            }

            Map<String, Integer> schoolMap = extractMap(userProfile, "school");
            Map<String, String> personalDataMap = extractMap(userProfile, "personal_data");
            if (schoolMap == null || personalDataMap == null) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Invalid profile data");
            }

            if (!validatePersonalData(personalDataMap)) {
                return errorResponse(HttpStatus.BAD_REQUEST, "Invalid full name or email");
            }
            personalData.setFull_name(personalDataMap.get("full_name"));
            personalData.setEmail(personalDataMap.get("email"));

            School school = getSchoolFromProfile(schoolMap);
            if (school == null) {
                return errorResponse(HttpStatus.NOT_FOUND, "School not found");
            }
            personalData.setSchool(school);

            updatePersonalData(personalData);
            user.setPersonal_data(personalData);
            updateUser(user);

            return mapPersonalData(auth, personalData, user);
        } catch (Exception e) {
            log.error("An error occurred while updating personal data", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    private boolean validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            log.error("Invalid username: {}", username);
            return false;
        }
        return true;
    }

    private boolean validatePersonalData(Map<String, String> personalDataMap) {
        String fullName = personalDataMap.get("full_name");
        String email = personalDataMap.get("email");
        if (fullName == null || fullName.isEmpty()) {
            log.error("Invalid full name: {}", fullName);
            return false;
        }
        if (email == null || email.isEmpty()) {
            log.error("Invalid email: {}", email);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private <T> T extractMap(Map<String, ?> data, String key) {
        Object value = data.get(key);
        if (value instanceof Map) {
            return (T) value;
        }
        return null;
    }

    private School getSchoolFromProfile(Map<String, Integer> schoolMap) {
        Integer schoolId = schoolMap.get("id");
        if (schoolId == null) {
            log.error("Missing school ID");
            return null;
        }
        School school = getSchoolById(schoolId);
        if (school == null) {
            log.error("School not found for ID: {}", schoolId);
        }
        return school;
    }

    private ResponseEntity<Map<String, ?>> errorResponse(HttpStatus status, String errorMessage) {
        log.error(errorMessage);
        return ResponseEntity.status(status).body(Map.of("error", errorMessage));
    }

    private int getUserId(Map<String, ?> userProfile) {
        Object userId;
        try{
            userId = userProfile.get("id");
            if(userId == null) throw new Exception("user id is null");
            if(!(userId instanceof Integer)) throw new Exception("user id is not an integer");
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
        return (int) userId;
    }
}
