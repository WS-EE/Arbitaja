package com.arbitaja.backend.users;

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

    public ResponseEntity<Map<String, ?>> mapPersonalData(Authentication auth, Personal_data personalData){
        if(personalData == null){
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("username", auth.getName(),"roles", auth.getAuthorities(),
                    "personal_data", Map.of("full_name", "", "email", "",
                            "school", Map.of("id", "", "name", ""))));
        }
        return ResponseEntity.ok(Map.of("username", auth.getName(),"roles", auth.getAuthorities(), "personal_data",
                Map.of("full_name", personalData.getFull_name(), "email", personalData.getEmail()),
                "school", Map.of("id", personalData.getSchool().getId(), "name", personalData.getSchool().getName())));
    }

    public ResponseEntity<Map<String, ?>> updatePersonalData(Authentication auth, Map<String, ?> userProfile){
        log.info("Updating user profile: {}", userProfile);

        String username = auth.getName();
        User user = getUserByUsername(username);

        if (user == null) {
            log.error("User not found: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        String newUsername = (String) userProfile.get("username");
        if (newUsername == null || newUsername.isEmpty()) {
            log.error("Invalid username: {}", newUsername);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Invalid username"));
        }
        user.setUsername(newUsername);

        Personal_data personalData = user.getPersonal_data();
        if (personalData == null) {
            personalData = new Personal_data();
        }

        Map<String, Integer> schoolMap = (Map<String, Integer>) userProfile.get("school");
        Map<String, String> personalDataMap = (Map<String, String>) userProfile.get("personal_data");

        if (schoolMap == null || personalDataMap == null) {
            log.error("Invalid profile data: school={}, personalDataMap={}", schoolMap, personalDataMap);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Invalid profile data"));
        }

        personalData.setFull_name(personalDataMap.get("full_name"));
        personalData.setEmail(personalDataMap.get("email"));

        Integer schoolId = schoolMap.get("id");

        School school = getSchoolById(schoolId);
        if (school == null) {
            log.error("School not found for ID: {}", schoolId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","School not found"));
        }


        personalData.setSchool(school);
        updatePersonalData(personalData);
        user.setPersonal_data(personalData);
        updateUser(user);

        return mapPersonalData(auth, personalData);
    }
}
