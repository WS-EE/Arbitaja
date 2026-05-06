package com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.request;

import java.util.List;

public record OverwriteCompetitionCompetitorsRequest(
    List<Integer> competitorIds
) {
}

