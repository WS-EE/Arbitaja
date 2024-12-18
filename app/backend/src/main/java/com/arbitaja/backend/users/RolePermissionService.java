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
        Optional<Role> roleOpt = roleRepository.findByName("user");
        Optional<Role> roleOptAdmin = roleRepository.findByName("admin");
        if (roleOpt.isPresent() && roleOptAdmin.isPresent()) {
            Role role = roleOpt.get();
            Role roleAdmin = roleOptAdmin.get();
            role = entityManager.merge(role);
            roleAdmin = entityManager.merge(roleAdmin);

            Optional<Permission> permissionOpt = permissionRepository.findByKey("basic");
            Optional<Permission> permissionOptAdmin = permissionRepository.findByKey("admin");
            if (permissionOpt.isPresent() && permissionOptAdmin.isPresent()) {
                Permission permission = permissionOpt.get();
                Permission permissionAdmin = permissionOptAdmin.get();
                permission = entityManager.merge(permission);
                permissionAdmin = entityManager.merge(permissionAdmin);

                log.info("Permission found: " + permission);

                Role_permissions role_permissions = new Role_permissions(permission, role, null);
                Role_permissions role_permissionsAdmin = new Role_permissions(permissionAdmin, roleAdmin, null);
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

