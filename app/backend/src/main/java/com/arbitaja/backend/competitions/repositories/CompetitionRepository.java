package com.arbitaja.backend.competitions.repositories;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;


public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    Competition findByid(Integer id);
    Competition findByName(String name);

    @Query(value = """
    SELECT co.id, co.name, co.organizer_id, co.start_time, co.end_time, co.scoring_criteria_group_main_id, co.score_showtime
    FROM competition co
    JOIN competitor_competition cc ON cc.competition_id = co.id
    JOIN competitor c ON cc.competitor_id = c.id
    WHERE c.id = :competitorId
    

    """, nativeQuery = true)
    Set<Competition> findByCompetitorId(Integer competitorId);
}
