package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.Role_permissions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolePermissionsRepository extends JpaRepository<Role_permissions, Integer> {
    Optional<Role_permissions> findRole_permissionsById(Integer id);
}
