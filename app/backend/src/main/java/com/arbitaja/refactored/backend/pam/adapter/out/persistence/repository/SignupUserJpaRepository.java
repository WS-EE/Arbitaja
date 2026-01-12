package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.SignupUserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for SignupUserJpaEntity.
 */
@Repository
public interface SignupUserJpaRepository extends JpaRepository<SignupUserJpaEntity, Integer> {
    Optional<SignupUserJpaEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}

