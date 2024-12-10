package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findPermissionById(Integer id);
    Optional<Permission> findPermissionByName(String name);
}
