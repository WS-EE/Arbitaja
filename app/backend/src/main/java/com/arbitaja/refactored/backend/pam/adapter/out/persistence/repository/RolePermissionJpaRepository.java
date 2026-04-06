package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RolePermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for RolePermissionJpaEntity.
 */
@Repository
public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionJpaEntity, Integer> {

    List<RolePermissionJpaEntity> findByRoleId(Integer roleId);
}

