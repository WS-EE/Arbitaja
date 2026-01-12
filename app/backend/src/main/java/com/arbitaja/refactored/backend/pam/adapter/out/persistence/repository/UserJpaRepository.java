package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for UserJpaEntity.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Integer> {
    Optional<UserJpaEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}

