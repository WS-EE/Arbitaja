package com.arbitaja.refactored.backend.scoring.core.port.out.history;

import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import lombok.NonNull;

/**
 * Output port for inserting scoring history entries.
 */
public interface ScoringHistoryRepositoryPort {

    ScoringHistoryEntry save(@NonNull ScoringHistoryEntry entry);
}
