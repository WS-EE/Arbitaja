package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for RoleJpaEntity.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Integer> {
    Optional<RoleJpaEntity> findByName(String name);

    @Query("SELECT r FROM RoleJpaEntity r JOIN r.userRoles ur WHERE ur.user.id = :userId")
    List<RoleJpaEntity> findByUserId(Integer userId);
}

