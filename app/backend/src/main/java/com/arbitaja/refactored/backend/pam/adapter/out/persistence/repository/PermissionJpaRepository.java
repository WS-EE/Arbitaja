package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for PermissionJpaEntity.
 */
@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, Integer> {

    @Query("""
            SELECT DISTINCT p FROM PermissionJpaEntity p
            JOIN p.rolePermissions rp
            JOIN rp.role r
            JOIN r.userRoles ur
            WHERE ur.user.id = :userId
        """)
    List<PermissionJpaEntity> findByUserId(Integer userId);
}

