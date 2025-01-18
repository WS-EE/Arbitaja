package com.arbitaja.backend.competitions.repositories;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
}
