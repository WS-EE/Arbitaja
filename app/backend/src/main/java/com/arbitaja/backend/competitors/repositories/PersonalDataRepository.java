package com.arbitaja.backend.competitors.repositories;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalDataRepository extends JpaRepository<Personal_data, Integer> {
    @Modifying
    @Query(value = """
    INSERT INTO personal_data (id, full_name, email)
        VALUES (:#{#personalData.id}, :#{#personalData.full_name}, :#{#personalData.email})
        ON CONFLICT (id)
        DO UPDATE SET
            full_name = EXCLUDED.full_name,
            email = EXCLUDED.email
    """, nativeQuery = true)
    void updateOrCreatePersonal_data(@Param("personalData") Personal_data personalData);

}
