package com.arbitaja.backend.users.APIs;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.PersonalDataRepository;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import com.arbitaja.backend.users.dataobjects.SignupUser;
import com.arbitaja.backend.users.dataobjects.User;
import com.arbitaja.backend.users.repositories.SignupUserRepository;
import com.arbitaja.backend.users.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SignupUserRepository signupUserRepository;

    public User getUserByUsername(String username){
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    public User getUserById(int id){
        if(id == 0) return null;
        Optional<User> user = userRepository.findById(id);
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

    public ResponseEntity<Map<String, ?>> updatePersonalData(Authentication auth, User sentUser) {
        log.info("Updating user profile: {}", sentUser);
        try {
            User user = getUserById(sentUser.getId());
            if (user == null) return errorResponse(HttpStatus.NOT_FOUND, "User not found");
            String newUsername = sentUser.getUsername();

            
            if (newUsername == null) return errorResponse(HttpStatus.BAD_REQUEST, "Invalid username");
            user.setUsername(newUsername);


            Personal_data personalData = user.getPersonal_data();
            if (personalData == null) personalData = new Personal_data();


            Personal_data personalDataMap = sentUser.getPersonal_data();
            if (personalDataMap == null) return errorResponse(HttpStatus.BAD_REQUEST, "Invalid personal data");


            School schoolMap = personalDataMap.getSchool();
            if (schoolMap == null) return errorResponse(HttpStatus.BAD_REQUEST, "Invalid school");


            if (personalDataMap.getEmail().isEmpty() || personalDataMap.getFull_name().isEmpty()) return errorResponse(HttpStatus.BAD_REQUEST, "Invalid full name or email");
            personalData.setFull_name(personalDataMap.getFull_name());
            personalData.setEmail(personalDataMap.getEmail());


            School school = getSchoolById(schoolMap.getId());
            if (school == null) return errorResponse(HttpStatus.NOT_FOUND, "School not found");
            personalData.setSchool(school);


            if(personalData.getCreated_at() == null) personalData.setCreated_at(Timestamp.from(Instant.now()));

            updatePersonalData(personalData);
            user.setPersonal_data(personalData);
            updateUser(user);

            return mapPersonalData(auth, personalData, user);
        } catch (Exception e) {
            log.error("An error occurred while updating personal data", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }

    public ResponseEntity<Map<String, ?>> signupUser(SignupUser sentUser) {
        try{
            if(signupUserRepository.findByUsername(sentUser.getUsername()).isPresent()
                    || userRepository.findUserByUsername(sentUser.getUsername()) != null) throw new Exception("User with username already exists");
            String saltedPassword = passwordEncoder.encode(sentUser.getSalted_password());
            sentUser.setSalted_password(saltedPassword);
            sentUser.setCreatedAt(Instant.now());
            sentUser.setIsApproved(false);
            log.info("Creating new user: {}", sentUser);
            signupUserRepository.save(sentUser);
            return ResponseEntity.ok(Map.of("message", "User created successfully"));
        } catch (Exception e){
            log.error("An error occurred while creating user{}", e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() != null ? e.getMessage() : "An error occurred");
        }
    }

    public ResponseEntity<Map<String, ?>> confirmUser(int id) {
        try{
            Optional<SignupUser> signupUser = signupUserRepository.findById(id);
            if(signupUser.isEmpty()) return errorResponse(HttpStatus.NOT_FOUND, "User not found");
            SignupUser user = signupUser.get();
            User newUser = new User(user.getPersonal_data(), user.getSalted_password(), user.getUsername(), null);
            user.setIsApproved(true);
            userRepository.save(newUser);
            signupUserRepository.delete(user);
            return ResponseEntity.ok(Map.of("message", "User approved successfully"));
        } catch (Exception e){
            log.error("An error occurred while confirming user{}", e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() != null ? e.getMessage() : "An error occurred");
        }
    }

    public List<SignupUser> signupUserList() {
        return signupUserRepository.findAll();
    }

    private ResponseEntity<Map<String, ?>> errorResponse(HttpStatus status, String errorMessage) {
        log.error(errorMessage);
        return ResponseEntity.status(status).body(Map.of("error", errorMessage));
    }
}
