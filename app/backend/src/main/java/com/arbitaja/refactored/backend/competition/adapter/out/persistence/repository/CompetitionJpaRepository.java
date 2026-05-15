package com.arbitaja.refactored.backend.competition.adapter.out.persistence.repository;

import com.arbitaja.refactored.backend.competition.adapter.out.persistence.entity.CompetitionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface CompetitionJpaRepository extends JpaRepository<CompetitionJpaEntity, Integer> {
    CompetitionJpaEntity findByName(String name);

    Page<CompetitionJpaEntity> findByNameContainingIgnoreCase(String search, Pageable pageable);

    @Query("SELECT c FROM CompetitionJpaEntity c " +
           "WHERE (:search IS NULL OR :search = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR :status = '' OR :status = 'ALL' " +
           "OR (:status = 'UPCOMING' AND c.startTime > :now) " +
           "OR (:status = 'ONGOING' AND c.startTime <= :now AND c.endTime >= :now) " +
           "OR (:status = 'FINISHED' AND c.endTime < :now))")
    Page<CompetitionJpaEntity> findBySearchAndStatus(
        @Param("search") String search,
        @Param("status") String status,
        @Param("now") Timestamp now,
        Pageable pageable
    );
}

