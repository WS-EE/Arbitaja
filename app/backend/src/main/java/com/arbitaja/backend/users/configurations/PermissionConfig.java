package com.arbitaja.backend.users.configurations;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import com.arbitaja.backend.competitors.dataobjects.School;
import com.arbitaja.backend.competitors.repositories.PersonalDataRepository;
import com.arbitaja.backend.competitors.repositories.SchoolRepository;
import com.arbitaja.backend.users.RolePermissionService;
import com.arbitaja.backend.users.dataobjects.*;
import com.arbitaja.backend.users.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Configuration
public class PermissionConfig {
    private static final Logger log = LogManager.getLogger(PermissionConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner commandLineRunner(
            PermissionRepository permissionRepository,
            RolePermissionsRepository rolePermissionsRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            RoleRelationRepository roleRelationRepository,
            RolePermissionService rolePermissionService,
            UserRepository userRepository,
            SchoolRepository schoolRepository,
            PersonalDataRepository personalDataRepository)

    {

        return args -> {
            // Clear existing data only if needed (this is for initial setup)
            rolePermissionsRepository.deleteAll();
            userRoleRepository.deleteAll();
            roleRelationRepository.deleteAll();
            permissionRepository.deleteAll();
            roleRepository.deleteAll();
            userRepository.deleteAll();

            // Create new Permission if it doesn't exist
            if (permissionRepository.count() == 0) {
                Permission permission = new Permission("basic", "basic", "description");
                Permission adminPermission = new Permission("admin", "admin", "description");
                permissionRepository.saveAll(List.of(permission, adminPermission));
                log.info("Permission created: {}", permission);
            } else {
                log.info("Permission already exists, skipping creation.");
            }

            // Create new Role if it doesn't exist
            if (roleRepository.count() == 0) {
                Role role = new Role("user", Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));
                Role admin = new Role("admin", Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));
                roleRepository.saveAll(List.of(role, admin));
                Role_relation role_relation = new Role_relation(admin, role);
                roleRelationRepository.saveAll(List.of(role_relation));
                log.info("Role created: {}", role);
            } else {
                log.info("Role already exists, skipping creation.");
            }

            if (schoolRepository.count() == 0) {
                School school = new School("RandomSchool", Timestamp.from(Instant.now()));
                schoolRepository.save(school);
            } else {
                log.info("School already exists, skipping creation.");
            }

            int personalDataId = -1;
            if(personalDataRepository.count() == 0) {
                Optional<School> optSchool = schoolRepository.findSchoolByName("RandomSchool");
                Personal_data personalData = new Personal_data();
                personalData.setFull_name("Arbitaja");
                personalData.setEmail("arbitaja@arbitaja.com");
                personalData.setCreated_at(Timestamp.from(Instant.now()));
                optSchool.ifPresent(personalData::setSchool);
                personalDataRepository.save(personalData);
                personalDataId = personalData.getId();
                log.info("Personal-data mapping created: {}", personalData);
            }

            rolePermissionService.createRolePermissionMapping();

            if (userRepository.count() == 0) {
                Optional<Personal_data> optPersonalData = personalDataRepository.findById(personalDataId);
                if (optPersonalData.isPresent()) {
                    Personal_data personalData = optPersonalData.get();
                    User user = new User(personalData, passwordEncoder.encode("123"), "Arbitaja", null);
                    userRepository.save(user);
                    log.info("User created: {}", user);
                }

            } else {
                log.info("User already exists, skipping creation.");
            }



            if (userRoleRepository.count() == 0) {
                Optional<User> userOpt = userRepository.findByUsername("Arbitaja");
                Optional<Role> roleOpt = roleRepository.findByName("admin");
                if(userOpt.isPresent() && roleOpt.isPresent()) {
                    User user = userOpt.get();
                    Role role = roleOpt.get();
                    User_role userRole = new User_role(user, role, Timestamp.from(Instant.now()));
                    userRoleRepository.save(userRole);
                    log.info("User-Role mapping created: {}", userRole);
                }
            } else {
                log.info("User-Role mapping already exists, skipping.");
            }
            User user = userRepository.findByUsername("Arbitaja").get();
            log.info("Users in the database: {}", userRepository.findAll());
            log.info("Permissions for user: {}", permissionRepository.findPermissionsByUserId(user.getId()));
        };
    }

}
