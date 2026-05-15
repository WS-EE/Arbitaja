package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RolePermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for RolePermissionJpaEntity.
 */
@Repository
public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionJpaEntity, Integer> {

    List<RolePermissionJpaEntity> findByRoleId(Integer roleId);

    @Modifying
    @Query("DELETE FROM RolePermissionJpaEntity rp WHERE rp.id IN :ids")
    void deleteByIdIn(@Param("ids") List<Integer> ids);
}

