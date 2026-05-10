package com.arbitaja.refactored.backend.scoring.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * One pre-aggregated point on the dashboard chart, produced directly by the database
 * window-function query: each row already carries the running total for that competitor
 * at that timestamp, so the application service does not need to re-accumulate per
 * criterion in memory.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResultRow {

    private Integer competitorId;
    private Timestamp timestamp;
    private Double runningTotal;
}
