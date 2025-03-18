package com.arbitaja.backend.competitions.scorings.repositories;

import com.arbitaja.backend.competitions.scorings.dataobjects.CompetitionScoringCriterion;
import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;


public interface CompetitionScoringCriterionRepository extends JpaRepository<CompetitionScoringCriterion, Integer> {
    @Query("SELECT csc.criteria FROM CompetitionScoringCriterion csc WHERE csc.competition.id = ?1")
    Set<ScoringCriterion> findAllByCompetitionId(Integer competition_id);
}
