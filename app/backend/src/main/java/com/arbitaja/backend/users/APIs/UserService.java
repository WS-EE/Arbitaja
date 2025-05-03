package com.arbitaja.backend.users.APIs;

import com.arbitaja.backend.GlobalExceptionHandler;
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

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PersonalDataRepository personalDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupUserRepository signupUserRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public UserService(UserRepository userRepository, SchoolRepository schoolRepository, PersonalDataRepository personalDataRepository, PasswordEncoder passwordEncoder, SignupUserRepository signupUserRepository,
                       RoleRepository roleRepository, UserRoleRepository userRoleRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.personalDataRepository = personalDataRepository;
        this.passwordEncoder = passwordEncoder;
        this.signupUserRepository = signupUserRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.permissionRepository = permissionRepository;
    }

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
        // Get the roles and permissions for the user
        List<Role> roles = roleRepository.findRolesByUserId(user.getId());
        List<Permission> permissions = permissionRepository.findPermissionsByUserId(user.getId());
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());

        // Add the roles to the authorities
        UserProfileResponse response = new UserProfileResponse(user.getId(), user.getUsername(), roles, authorities);

        // Map the personal data to the response
        UserProfileResponse.PersonalDataResponse personalDataResponse = new UserProfileResponse.PersonalDataResponse();
        // If the personal data is not null then set the values
        if (personalData != null) {
            personalDataResponse.setId(personalData.getId());
            personalDataResponse.setFullName(personalData.getFull_name());
            personalDataResponse.setEmail(personalData.getEmail());
        }
        // If the personal data is not null and the school is not null then set the values
        UserProfileResponse.SchoolResponse schoolResponse = new UserProfileResponse.SchoolResponse();
        if (personalData != null && personalData.getSchool() != null) {
            schoolResponse.setId(personalData.getSchool().getId());
            schoolResponse.setName(personalData.getSchool().getName());
            personalDataResponse.setSchool(schoolResponse);
        }

        response.setPersonalData(personalDataResponse);

        return response;
    }

    /**
     * Returns a list of all users in the database
     *
     * @return ResponseEntity of all users
     */
    public ResponseEntity<?> getAllUsers() {
        // Get all users from the database
        List<User> users = userRepository.findAll();
        Set<UserProfileResponse> userResponses = new HashSet<>();
        // Map the users to the response
        for (User user : users) {
            userResponses.add(mapPersonalData(user.getPersonal_data(), user));
        }
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }


    /**
     * Deletes the user with the given id
     *
     * @param id The ID of the User being deleted.
     * @return ResponseEntity of if the deletion was successful
     */
    @Transactional
    public ResponseEntity<?> deleteUser(int id) {
        Optional<User> user = userRepository.findById(id);
        // If the user does not exist throw an exception
        if (user.isEmpty()) throw new GlobalExceptionHandler.NotFoundException("User not found");
        userRepository.delete(user.get());
        return ResponseEntity.ok(Map.of("message", "User was deleted successfully"));
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
        // Check if the user exists
        User user = getUserById(sentUser.getId());
        // If the user does not exist throw an exception
        if (user == null) throw new GlobalExceptionHandler.NotFoundException("User not found");
        // Check if the user is the same as the authenticated user or if the authenticated user is an admin
        // if the authenticated user is not an admin and the authenticated user is not the same as the user being changed
        // then throw an exception
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("admin")) && !auth.getName().equals(user.getUsername())) {
            throw new GlobalExceptionHandler.UnauthorizedException("User not authorized to change other user");
        }

        // if the username is null or empty throw an exception
        if (sentUser.getUsername() == null) throw new IllegalArgumentException("Invalid username");
        user.setUsername(sentUser.getUsername());

        // Check if the personal data is null and create a new one if it is
        Personal_data personalData = user.getPersonal_data();
        if (user.getPersonal_data() == null) personalData = new Personal_data();

        // Check if the personal data is not null and set the new values
        Personal_data personalDataMap = sentUser.getPersonal_data();
        if (personalDataMap == null) throw new IllegalArgumentException("Invalid personal data");

        // If email or full name is null or empty then throw an exception
        if (personalDataMap.getEmail().isEmpty() || personalDataMap.getFull_name().isEmpty()) throw new IllegalArgumentException("Invalid full name or email");
        personalData.setFull_name(personalDataMap.getFull_name());
        personalData.setEmail(personalDataMap.getEmail());

        // Check if the school is null and set it to null if it is
        School school = personalDataMap.getSchool() == null ? null : getSchoolById(personalDataMap.getSchool().getId());
        if (school == null) throw new GlobalExceptionHandler.NotFoundException("School not found");
        personalData.setSchool(school);

        // Set the created at timestamp to now if it is null
        if(personalData.getCreated_at() == null) personalData.setCreated_at(Timestamp.from(Instant.now()));

        // Save the personal data
        updatePersonalData(personalData);
        user.setPersonal_data(personalData);
        updateUser(user);

        return ResponseEntity.ok(mapPersonalData(personalData, user));
    }


    /**
     * Used to make a new signup user
     *
     * @param sentUser new signup users data
     *
     * @return ResponseEntity of if the creation of the signup user was successful
     */
    public ResponseEntity<Map<String, ?>> signupUser(SignupUser sentUser) {
        log.info("Signing up user: {}", sentUser);
        // Check if the user with this username already exists in signup users and regular users
        // If the user with this username already exists throw an exception
        if(signupUserRepository.findByUsername(sentUser.getUsername()).isPresent()
                || userRepository.findUserByUsername(sentUser.getUsername()) != null) throw new GlobalExceptionHandler.DuplicateException("User with username already exists");
        // Encode the password
        String salted_password = passwordEncoder.encode(sentUser.getSalted_password());
        // Set the password and created at
        sentUser.setSalted_password(salted_password);
        sentUser.setCreatedAt(Instant.now());
        sentUser.setIsApproved(false);
        log.info("Creating new signup user: {}", sentUser);
        signupUserRepository.save(sentUser);
        return ResponseEntity.ok(Map.of("message", "User created successfully"));
    }

    /**
     * Used to decline a signup request by the admin
     * Deletes the signup user entity
     *
     * @param id id of signup user trying to be deleted
     *
     * @return ResponseEntity of if the deletion was successful
     */
    public ResponseEntity<Map<String, ?>> declineUser(Integer id) {
        log.info("Declining user with id: {}", id);
        // Check if the user exists
        Optional<SignupUser> deletableUser = signupUserRepository.findById(id);
        // If the user does not exist throw an exception
        if(deletableUser.isEmpty()) throw new GlobalExceptionHandler.NotFoundException("User not found");
        // If the user exists delete it
        signupUserRepository.delete(deletableUser.get());
        return ResponseEntity.ok(Map.of("message", "User declined successfully"));
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
        // Update SignupUser
        SignupUser user = setSignupUserChanges(sentUser);

        // Create and save new Personal_data
        Personal_data personalData = new Personal_data(user.getPersonal_data());
        personalData.setCreated_at(Timestamp.from(Instant.now()));
        personalData = personalDataRepository.save(personalData);

        // Create User with managed Personal_data
        User newUser = new User(user, personalData);
        newUser = userRepository.save(newUser);

        // Fetch role and create User_role
        giveUserBasicRole(newUser);

        // Delete SignupUser
        signupUserRepository.delete(user);

        return ResponseEntity.ok(Map.of("message", "User approved successfully"));
    }

    /**
     * Used to change a users password
     *
     * @param sentUser user with new password
     * @param auth authentication of the user
     *
     * @return ResponseEntity of if the password change was successful
     */
    @Transactional
    public ResponseEntity<?> changePassword(User sentUser, Authentication auth) {
        log.info("Changing password for user: {}", sentUser);
        // Check if the user exists
        Optional<User> userOpt = userRepository.findById(sentUser.getId());
        if(userOpt.isEmpty()) throw new GlobalExceptionHandler.NotFoundException("User not found");
        User user = userOpt.get();
        // check if the user is the same as the authenticated user or if the authenticated user is an admin
        // if the authenticated user is not an admin and the authenticated user is not the same as the user being changed
        // then throw an exception
        if(!auth.getName().equals(user.getUsername()) && !auth.getAuthorities().contains(new SimpleGrantedAuthority("admin")))
            throw new GlobalExceptionHandler.UnauthorizedException("Non admin accounts are not allowed to change other users passwords");
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
     * Used to create a new user
     *
     * @param sentUser new user data
     * @param isAdmin if the user being created should be an admin
     *
     * @return ResponseEntity of if the creation was successful
     */
    public ResponseEntity<?> createUser(User sentUser, Boolean isAdmin) {
        log.info("Creating new user: {}", sentUser);
        // Check if the user with this username already exists
        if (userRepository.findByUsername(sentUser.getUsername()).isPresent())
            throw new GlobalExceptionHandler.DuplicateException("User with username already exists");
        // Encode the password
        String salted_password = passwordEncoder.encode(sentUser.getSalted_password());
        sentUser.setSalted_password(salted_password);
        // Set the personal data
        savePersonalData(sentUser.getPersonal_data());
        userRepository.save(sentUser);
        // give the user the basic role
        giveUserBasicRole(sentUser);
        // if the user is an admin, make him an admin
        if(isAdmin) makeUserAdmin(sentUser);
        return ResponseEntity.ok(Map.of("message", "User created successfully"));
    }


    /**
     * Updating user signup from the admin
     *
     * @param sentUser updated user data
     *
     * @return updated signupUser
     */
    private SignupUser setSignupUserChanges(SignupUser sentUser){
        // Find the signup user by id
        Optional<SignupUser> signupUser = signupUserRepository.findById(sentUser.getId());
        // If the signup user is not found throw an exception
        if(signupUser.isEmpty()) throw new GlobalExceptionHandler.NotFoundException("User not found");
        SignupUser newUser = signupUser.get();
        // Set the new values
        newUser.setUsername(sentUser.getUsername());
        newUser.setPersonal_data(sentUser.getPersonal_data());
        // Set the school to null if the id is 0 or school is null
        if(newUser.getPersonal_data().getSchool() == null || newUser.getPersonal_data().getSchool().getId() == 0)
            newUser.getPersonal_data().setSchool(null);
        // approve the signup user
        newUser.setIsApproved(true);
        // save the changes to signup user
        signupUserRepository.save(newUser);
        return newUser;
    }

    /**
     * Used to save the personal data of a user
     * @param personalData personal data that is to be saved
     */
    private void savePersonalData(Personal_data personalData) {
        // If personal data is null return
        if (personalData == null) return;
        // If personal data is not null and school is not null save the school
        if (personalData.getSchool() != null) saveSchool(personalData);
        personalData.setCreated_at(Timestamp.from(Instant.now()));
        personalDataRepository.save(personalData);
    }

    /**
     * Used to save the school of a user
     * @param personalData personal data where the school is
     */
    private void saveSchool(Personal_data personalData) {
        // If given school has an id then find the school and set it to the personal data
        if(personalData.getSchool().getId() != null) {
            Optional<School> existingSchool = schoolRepository.findById(personalData.getSchool().getId());
            if(existingSchool.isPresent()) {
                School existing = existingSchool.get();
                personalData.setSchool(existing);
            }
            // If the school is not in the database then save it
            else{
                personalData.getSchool().setCreated_at(Timestamp.from(Instant.now()));
                personalData.getSchool().setId(null);
                schoolRepository.save(personalData.getSchool());
            }
        }
    }

    /**
     * Used to give the user the basic role
     * @param user user that is to be given the basic role
     */
    private void giveUserBasicRole(User user) {
        Role role = roleRepository.findByName("user").orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("User role not found"));
        User_role basic = new User_role(user, role);
        userRoleRepository.save(basic);
    }

    /**
     * Used to make a user an admin
     * @param user user that is to be made an admin
     */
    private void makeUserAdmin(User user) {
        Role role = roleRepository.findByName("admin").orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Admin role not found"));
        User_role userRole = new User_role(user, role);
        userRoleRepository.save(userRole);
    }
}
