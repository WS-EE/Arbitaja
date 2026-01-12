package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.SchoolJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for SchoolJpaEntity.
 */
@Repository
public interface SchoolJpaRepository extends JpaRepository<SchoolJpaEntity, Integer> {
}

