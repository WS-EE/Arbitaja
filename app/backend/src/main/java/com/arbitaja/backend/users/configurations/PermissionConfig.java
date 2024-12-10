package com.arbitaja.backend.users.configurations;


import com.arbitaja.backend.users.dataobjects.Permission;
import com.arbitaja.backend.users.dataobjects.Role;
import com.arbitaja.backend.users.dataobjects.Role_permissions;
import com.arbitaja.backend.users.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;
import java.time.Instant;

@Configuration
public class PermissionConfig {
    private static final Logger log = LogManager.getLogger(PermissionConfig.class);

    @Bean
    CommandLineRunner commandLineRunner(PermissionRepository permissionRepository, RolePermissionsRepository rolePermissionsRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, UserRepository userRepository) {

        return args -> {
            Permission permission = new Permission("name", "key", "description");
            permissionRepository.save(permission);
            Role role = new Role("name", Timestamp.from(Instant.now()), Timestamp.from(Instant.now()));
            roleRepository.save(role);
            Role_permissions role_permissions = new Role_permissions(permission, role, null);
            rolePermissionsRepository.save(role_permissions);




            log.info("Permissions in the database on startup: " + rolePermissionsRepository.findAll());
        };
    }
}
