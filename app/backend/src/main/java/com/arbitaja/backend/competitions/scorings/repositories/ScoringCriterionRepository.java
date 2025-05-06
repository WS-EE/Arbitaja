package com.arbitaja.backend.competitions.scorings.repositories;

import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringCriterion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ScoringCriterionRepository extends JpaRepository<ScoringCriterion, Integer> {

    @Query("SELECT sc FROM ScoringCriterion sc left join CompetitionScoringCriterion csc on sc.id = csc.criteria.id WHERE csc.competition.id = :competitionId")
    Set<ScoringCriterion> findByCompetitionId(Integer competitionId);
}
