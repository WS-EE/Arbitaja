package com.arbitaja.backend.competitors.APIs;

import com.arbitaja.backend.competitions.dataobjects.Competition;
import com.arbitaja.backend.competitors.dataobjects.Competitor;

public class CompetitionCompetitorWrapper {
    private Competition competition;
    private Competitor competitor;

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Competitor getCompetitor() {
        return competitor;
    }

    public void setCompetitor(Competitor competitor) {
        this.competitor = competitor;
    }
}
