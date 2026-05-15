package com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Integer> {
    Optional<UserJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM UserJpaEntity u LEFT JOIN u.personalData pd " +
           "WHERE (:search IS NULL OR :search = '' " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(pd.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UserJpaEntity> findBySearchTerm(@Param("search") String search, Pageable pageable);
}

