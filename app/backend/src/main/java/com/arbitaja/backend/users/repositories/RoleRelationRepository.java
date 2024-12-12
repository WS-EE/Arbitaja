package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.Role;
import com.arbitaja.backend.users.dataobjects.Role_relation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRelationRepository extends JpaRepository<Role_relation, Integer> {
    Optional<Role_relation> findByParentRole(Role parentRole);
}
