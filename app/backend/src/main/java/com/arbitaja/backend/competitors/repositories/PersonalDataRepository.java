package com.arbitaja.backend.competitors.repositories;

import com.arbitaja.backend.competitors.dataobjects.Personal_data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalDataRepository extends JpaRepository<Personal_data, Integer> {
}
