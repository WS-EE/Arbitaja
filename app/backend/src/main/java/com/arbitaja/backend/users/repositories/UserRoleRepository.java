package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.Role;
import com.arbitaja.backend.users.dataobjects.User_role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<User_role, Integer> {
    Optional<User_role> findByRole(Role role);
}
