package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.SignupUserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for SignupUserJpaEntity.
 */
@Repository
public interface SignupUserJpaRepository extends JpaRepository<SignupUserJpaEntity, Integer> {
    Optional<SignupUserJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT s FROM SignupUserJpaEntity s LEFT JOIN s.personalData pd " +
           "WHERE (:search IS NULL OR :search = '' " +
           "OR LOWER(s.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(pd.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<SignupUserJpaEntity> findBySearchTerm(@Param("search") String search, Pageable pageable);
}

