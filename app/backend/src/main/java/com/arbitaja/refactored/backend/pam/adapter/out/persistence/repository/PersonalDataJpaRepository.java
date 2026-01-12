package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PersonalDataJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for PersonalDataJpaEntity.
 */
@Repository
public interface PersonalDataJpaRepository extends JpaRepository<PersonalDataJpaEntity, Integer> {
}

