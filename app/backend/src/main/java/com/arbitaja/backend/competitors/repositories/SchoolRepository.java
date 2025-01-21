package com.arbitaja.backend.competitors.repositories;

import com.arbitaja.backend.competitors.dataobjects.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Integer> {
    Optional<School> findById(int id);
    Optional<School> findSchoolByName(String name);
    @Modifying
    @Query(value = """
    INSERT INTO school (id, name, created_at)
        VALUES (:#{#school.id}, :#{#school.name}, :#{#school.created_at})
        ON CONFLICT (id)
        DO UPDATE SET
            id = EXCLUDED.id,
            created_at = EXCLUDED.created_at
    """, nativeQuery = true)
    void updateSchool(School school);
}
