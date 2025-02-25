package com.arbitaja.backend.competitions.scorings.repositories;

import com.arbitaja.backend.competitions.scorings.dataobjects.Scoring_groups_structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ScoringGroupsStructureRepository extends JpaRepository<Scoring_groups_structure, Integer> {

    @Query(value = """
            SELECT sgs.*
    FROM scoring_groups_structure sgs
    WHERE sgs.id IN (
            SELECT cs.id
                    FROM scoring_groups_structure cs
                    JOIN competition c ON c.scoring_criteria_group_main_id = cs.id or c.scoring_criteria_group_main_id = cs.scoring_parent_group_id
                    WHERE c.id = :competitionId
    );
    """, nativeQuery = true)
    Set<Scoring_groups_structure> findAllByCompetitionId(@Param("competitionId") int competitionId);

    Scoring_groups_structure findByName(String name);
}
