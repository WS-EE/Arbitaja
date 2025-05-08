package com.arbitaja.backend.competitions.scorings.repositories;

import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoringCriterionRepository extends JpaRepository<ScoringCriterion, Integer> {
}
