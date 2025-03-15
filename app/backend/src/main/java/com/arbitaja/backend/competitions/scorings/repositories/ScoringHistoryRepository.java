package com.arbitaja.backend.competitions.scorings.repositories;

import com.arbitaja.backend.competitions.scorings.dataobjects.ScoringHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public interface ScoringHistoryRepository extends JpaRepository<ScoringHistory, Integer> {
    ScoringHistory findByCompetitionId(Integer competition_id);

    @Query("SELECT sh FROM ScoringHistory sh WHERE sh.competition.id = :competitionId AND sh.competitor.id = :competitorId AND sh.createdAt <= :scoreShowtime")
    Set<ScoringHistory> findByCompetitionIdAndCompetitorIdAndScoreShowtime(Integer competitionId, Integer competitorId, Timestamp scoreShowtime);

    default Set<ScoringHistory> findByCompetitionIdAndCompetitorIdOrderedByCreatedAt(Integer competitionId, Integer competitorId, Timestamp scoreShowtime) {
        Set<ScoringHistory> scoringHistories = findByCompetitionIdAndCompetitorIdAndScoreShowtime(competitionId, competitorId, scoreShowtime);
        return new TreeSet<>(Comparator.comparing(ScoringHistory::getCreatedAt));
    }
}
