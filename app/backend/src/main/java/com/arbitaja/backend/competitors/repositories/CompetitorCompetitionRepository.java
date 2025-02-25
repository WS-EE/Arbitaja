package com.arbitaja.backend.competitors.repositories;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitors.dataobjects.Competitor;
import com.arbitaja.backend.competitors.dataobjects.Competitor_competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitorCompetitionRepository extends JpaRepository<Competitor_competition, Integer> {

    Competitor_competition findByCompetitorAndCompetition(Competitor competitor, Competition competition);
}