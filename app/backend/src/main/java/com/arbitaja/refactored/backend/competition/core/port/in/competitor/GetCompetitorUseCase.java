package com.arbitaja.refactored.backend.competition.core.port.in.competitor;

import com.arbitaja.refactored.backend.competition.core.domain.model.Competitor;
import java.util.Set;

public interface GetCompetitorUseCase {

  Competitor getCompetitorById(Integer id);

  Set<Competitor> getCompetitorsByCompetitionId(Integer id);

  Set<Competitor> getAllCompetitors();
}
