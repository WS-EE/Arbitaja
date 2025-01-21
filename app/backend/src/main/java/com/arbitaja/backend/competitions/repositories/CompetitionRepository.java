package com.arbitaja.backend.competitions.repositories;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    Competition findByid(Integer id);
    Competition findByName(String name);
    @Modifying
    @Query(value = """
    INSERT INTO competition (id, name, organizer_id, start_time, end_time)
        VALUES (:#{#competition.id}, :#{#competition.name}, :#{#competition.organizer_id}, :#{#competition.start_time}, :#{#competition.end_time})
        ON CONFLICT (id)
        DO UPDATE SET
            id = EXCLUDED.id
    """, nativeQuery = true)
    void updateCompetition(Competition competition);
}
