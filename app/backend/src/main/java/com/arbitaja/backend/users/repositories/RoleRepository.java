package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.Role;
import com.arbitaja.backend.users.dataobjects.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);

    @Query(value = """
        WITH RECURSIVE role_hierarchy AS (
            SELECT r.id AS role_id, r.name AS role_name, r.created_at AS created_at, r.changed_at AS changed_at
            FROM role r
            JOIN user_role ur ON r.id = ur.role_id
            WHERE ur.user_id = :userId

            UNION

            SELECT rr.child_role_id AS role_id, r.name AS role_name, r.created_at AS created_at, r.changed_at AS changed_at
            FROM role_hierarchy rh
            JOIN role_relation rr ON rh.role_id = rr.parent_role_id
            JOIN role r ON rr.child_role_id = r.id
        )
        SELECT DISTINCT rh.role_id AS id, rh.role_name AS name, rh.created_at, rh.changed_at
        FROM role_hierarchy rh;
        """, nativeQuery = true)
    List<Role> findRolesByUserId(@Param("userId") int userId);
}
