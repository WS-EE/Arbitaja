package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findPermissionById(Integer id);
    Optional<Permission> findPermissionByName(String name);
    @Query(value = """
        WITH RECURSIVE role_hierarchy AS (
            SELECT r.id AS role_id
            FROM role r
            JOIN user_role ur ON r.id = ur.role_id
            WHERE ur.user_id = :userId

            UNION

            SELECT rr.child_role_id
            FROM role_hierarchy rh
            JOIN role_relation rr ON rh.role_id = rr.parent_role_id
        )
        SELECT DISTINCT p.id, p.name, p.key, p.key_object
        FROM role_hierarchy rh
        JOIN role_permissions rp ON rh.role_id = rp.role_id
        JOIN permission p ON rp.permission_id = p.id;
        """, nativeQuery = true)
    List<Permission> findPermissionsByUserId(@Param("userId") int userId);

    Optional<Permission> findByKey(String key);
}
