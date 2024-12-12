package com.arbitaja.backend.users;
import com.arbitaja.backend.users.dataobjects.Permission;
import com.arbitaja.backend.users.dataobjects.Role;
import com.arbitaja.backend.users.dataobjects.Role_permissions;
import com.arbitaja.backend.users.repositories.PermissionRepository;
import com.arbitaja.backend.users.repositories.RolePermissionsRepository;
import com.arbitaja.backend.users.repositories.RoleRepository;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolePermissionService {
    private static final Logger log = LogManager.getLogger(RolePermissionService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionsRepository rolePermissionsRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional  // Ensure this method runs within a transaction
    public void createRolePermissionMapping() {
        // Retrieve the Role by name
        Optional<Role> roleOpt = roleRepository.findByName("user");
        Optional<Role> roleOptAdmin = roleRepository.findByName("admin");
        if (roleOpt.isPresent() && roleOptAdmin.isPresent()) {
            Role role = roleOpt.get();
            Role roleAdmin = roleOptAdmin.get();
            // Reattach the Role entity to the session (if detached)
            role = entityManager.merge(role); // Ensure entity is managed
            roleAdmin = entityManager.merge(roleAdmin);

            // Retrieve the Permission by key
            Optional<Permission> permissionOpt = permissionRepository.findByKey("basic");
            Optional<Permission> permissionOptAdmin = permissionRepository.findByKey("admin");
            if (permissionOpt.isPresent() && permissionOptAdmin.isPresent()) {
                Permission permission = permissionOpt.get();
                Permission permissionAdmin = permissionOptAdmin.get();
                // Reattach the Permission entity to the session (if detached)
                permission = entityManager.merge(permission); // Ensure entity is managed
                permissionAdmin = entityManager.merge(permissionAdmin);

                log.info("Permission found: " + permission);

                // Create Role_permissions mapping
                Role_permissions role_permissions = new Role_permissions(permission, role, null);
                Role_permissions role_permissionsAdmin = new Role_permissions(permissionAdmin, roleAdmin, null);
                // Save the Role_permissions entity
                rolePermissionsRepository.saveAll(List.of(role_permissions, role_permissionsAdmin));

                log.info("Role-Permissions mapping created: " + role_permissions);
                log.info("Role-Permissions mapping created: " + role_permissionsAdmin);
            } else {
                log.error("Permission not found with key 'key'");
            }
        } else {
            log.error("Role not found with name 'name'");
        }

    }
}

