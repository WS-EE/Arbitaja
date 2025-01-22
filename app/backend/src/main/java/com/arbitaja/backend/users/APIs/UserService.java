package com.arbitaja.backend.users.APIs;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.PersonalDataRepository;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import com.arbitaja.backend.users.APIs.responses.UserProfileResponse;
import com.arbitaja.backend.users.dataobjects.*;
import com.arbitaja.backend.users.repositories.*;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * If user with given username exists returns the user otherwise returns null
     *
     * @param username username for the User being searched for
     * @return Found User or null
     */
    public User getUserByUsername(String username){
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    /**
     * If user with given id exists returns the user otherwise returns null
     *
     * @param id The ID of the User being searched for.
     * @return Found User or null
     */
    public User getUserById(int id){
        if(id == 0) return null;
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    /**
     * If School with given id exists returns the user otherwise returns null
     *
     * @param id The ID of the School being searched for.
     * @return Found School or null
     */
    public School getSchoolById(int id) {
        log.debug("Getting school with ID: {}", id);
        Optional<School> school = schoolRepository.findById(id);
        return school.orElse(null);
    }

    /**
     * Updates a users parameters in the database
     *
     * @param user User that is being updated
     */
    public void updateUser(User user){
        userRepository.updateUser(user);
    }


    /**
     * If given personal data exists in the database then update the fields otherwise create a new personal_data entity in the database
     *
     * @param personalData personal data that is being updated
     */
    public void updatePersonalData(Personal_data personalData){
        personalDataRepository.updateOrCreatePersonal_data(personalData);
    }


    /**
     * Returns the appropriate response for user and it's personal data
     *
     * @param personalData personal data that is to be returned
     * @param user user for getting username, roles and permissions
     *
     * @return ResponseEntity of the users data
     */
    public UserProfileResponse mapPersonalData(Personal_data personalData, User user) {
        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        List<Permission> permissions = permissionRepository.findPermissionsByUserId(user.getId());
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());

        UserProfileResponse response = new UserProfileResponse(user.getId(), user.getUsername(), roles, authorities);

        UserProfileResponse.PersonalDataResponse personalDataResponse = new UserProfileResponse.PersonalDataResponse();
        if (personalData != null) {
            personalDataResponse.setFullName(personalData.getFull_name());
            personalDataResponse.setEmail(personalData.getEmail());
        }

        UserProfileResponse.SchoolResponse schoolResponse = new UserProfileResponse.SchoolResponse();
        if (personalData != null && personalData.getSchool() != null) {
            schoolResponse.setId(personalData.getSchool().getId());
            schoolResponse.setName(personalData.getSchool().getName());
            personalDataResponse.setSchool(schoolResponse);
        }

        response.setPersonalData(personalDataResponse);

        return response;
    }

    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        Set<UserProfileResponse> userResponses = new HashSet<>();
        for (User user : users) {
            userResponses.add(mapPersonalData(user.getPersonal_data(), user));
        }
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<?> deleteUser(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return ResponseEntity.ok(Map.of("message", "User was deleted successfully"));
        } else {
            return new ResponseEntity<>(Map.of("error", "User not found"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates the users data given in the request.
     *
     * @param auth User who made the request
     * @param sentUser Changed user data
     *
     * @return ResponseEntity of the new users data
     */
    public ResponseEntity<?> updatePersonalData(Authentication auth, User sentUser) {
        log.info("Updating user profile: {}", sentUser);
        try {
            User user = getUserById(sentUser.getId());
            if (user == null) return errorResponse(HttpStatus.NOT_FOUND, "User not found");
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin")) && !auth.getName().equals(user.getUsername())) {
                return errorResponse(HttpStatus.FORBIDDEN, "User not authorized to change other user");
            }
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

            return ResponseEntity.ok(mapPersonalData(personalData, user));
        } catch (Exception e) {
            log.error("An error occurred while updating personal data", e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        }
    }


    /**
     * Used to make a new signup user
     *
     * @param sentUser new signup users data
     *
     * @return ResponseEntity of if the creation of the signup user was successful
     */
    public ResponseEntity<Map<String, ?>> signupUser(SignupUser sentUser) {
        try{
            log.info("Signing up user: {}", sentUser);
            if(signupUserRepository.findByUsername(sentUser.getUsername()).isPresent()
                    || userRepository.findUserByUsername(sentUser.getUsername()) != null) throw new Exception("User with username already exists");
            String salted_password = passwordEncoder.encode(sentUser.getSalted_password());
            sentUser.setSalted_password(salted_password);
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

    /**
     * Used to decline a signup request by the admin
     * Deletes the signup user entity
     *
     * @param sentUser signup user that is to be deleted
     *
     * @return ResponseEntity of if the deletion was successful
     */
    public ResponseEntity<Map<String, ?>> declineUser(SignupUser sentUser) {
        try{
            signupUserRepository.delete(sentUser);
            return ResponseEntity.ok(Map.of("message", "User declined successfully"));
        } catch (Exception e){
            log.error("An error occurred while declining user{}", e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() != null ? e.getMessage() : "An error occurred");
        }
    }

    /**
     * Used for approving a signup request by the admin
     * Makes a new user from the signup user data and deletes the signup user entity
     *
     * @param sentUser signup user that is to be accepted
     *
     * @return ResponseEntity of if the approval was successful
     */
    public ResponseEntity<Map<String, ?>> approveUser(SignupUser sentUser) {
        try {
            // Update SignupUser
            SignupUser user = setSignupUserChanges(sentUser);

            // Create and save new Personal_data
            Personal_data personalData = new Personal_data(user.getPersonal_data());
            personalData = personalDataRepository.save(personalData);

            // Create User with managed Personal_data
            User newUser = new User(user, personalData);
            newUser = userRepository.save(newUser);

            // Fetch role and create User_role
            Optional<Role> role = roleRepository.findByName("user");
            if (role.isEmpty()) {
                return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "role user not found");
            }

            User_role basic = new User_role(newUser, role.get());
            userRoleRepository.save(basic);

            // Delete SignupUser
            signupUserRepository.delete(user);

            return ResponseEntity.ok(Map.of("message", "User approved successfully"));
        } catch (Exception e) {
            log.error("An error occurred while confirming user: {}", e.getMessage(), e);
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage() != null ? e.getMessage() : "An error occurred");
        }
    }

    @Transactional
    public ResponseEntity<?> changePassword(User sentUser, Authentication auth) {
        Optional<User> userOpt = userRepository.findByUsername(sentUser.getUsername());
        if(userOpt.isEmpty()) return errorResponse(HttpStatus.NOT_FOUND, "User not found");
        User user = userOpt.get();
        if(auth != null && auth.getName().equals(user.getUsername()) && !auth.getAuthorities().contains(new SimpleGrantedAuthority("admin"))) return errorResponse(HttpStatus.FORBIDDEN, "You are not allowed to change password");
        user.setSalted_password(passwordEncoder.encode(sentUser.getSalted_password()));
        updateUser(user);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }


    /**
     * used to get a list of signup users
     *
     * @return List of SignupUsers
     */
    public List<SignupUser> signupUserList() {
        return signupUserRepository.findAll();
    }


    /**
     * Helper method for creating error ResponseEntities
     *
     * @param status status code for error
     * @param errorMessage message for error
     *
     * @return ResponseEntity of the error
     */
    private ResponseEntity<Map<String, ?>> errorResponse(HttpStatus status, String errorMessage) {
        log.error(errorMessage);
        return ResponseEntity.status(status).body(Map.of("error", errorMessage));
    }


    /**
     * Updating user signup from the admin
     *
     * @param sentUser updated user data
     *
     * @return updated signupUser
     */
    private SignupUser setSignupUserChanges(SignupUser sentUser) throws Exception {
        Optional<SignupUser> signupUser = signupUserRepository.findById(sentUser.getId());
        if(signupUser.isEmpty()) throw new Exception("User not found");
        SignupUser newUser = signupUser.get();
        newUser.setUsername(sentUser.getUsername());
        newUser.setPersonal_data(sentUser.getPersonal_data());
        if(newUser.getPersonal_data().getSchool() == null || newUser.getPersonal_data().getSchool().getId() == 0) newUser.getPersonal_data().setSchool(null);
        newUser.setIsApproved(true);
        signupUserRepository.save(newUser);
        return newUser;
    }
}
