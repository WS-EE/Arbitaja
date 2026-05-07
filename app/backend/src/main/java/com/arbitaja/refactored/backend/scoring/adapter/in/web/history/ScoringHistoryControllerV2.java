package com.arbitaja.refactored.backend.scoring.adapter.in.web.history;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.annotations.RequiresScoringPermission;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.request.AddScoringHistoryRequest;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.response.ScoringHistoryEntryResponse;
import com.arbitaja.refactored.backend.scoring.adapter.util.ScoringHistoryWebMapper;
import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import com.arbitaja.refactored.backend.scoring.core.port.in.history.RecordScoringHistoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound REST adapter for recording scoring history entries.
 */
@RestController
@RequestMapping("/v2/scoring/history")
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "arbitaja.scoring.mode", havingValue = "hex", matchIfMissing = true)
public class ScoringHistoryControllerV2 {

    private final RecordScoringHistoryUseCase recordScoringHistoryUseCase;
    private final ScoringHistoryWebMapper mapper;

    @PostMapping
    @Operation(summary = "Record a scoring result", description = "Records a new scoring history entry for a competitor")
    @Parameters({
        @Parameter(name = "x-api-key", description = "API key for authentication", required = true)
    })
    @RequiresScoringPermission(ScoringPermissionCode.RECORD_SCORING_RESULTS)
    public ResponseEntity<ScoringHistoryEntryResponse> recordScore(
            @RequestHeader(name = "x-api-key") String apiKey,
            @RequestBody @Valid AddScoringHistoryRequest request) {
        log.info("Recording score for competition={} competitor={} criterion={} points={}",
            request.competitionId(), request.competitorId(), request.criteriaId(), request.points());
        return ResponseEntity.status(201).body(
            mapper.toResponse(recordScoringHistoryUseCase.recordScore(mapper.toCommand(request)))
        );
    }
}
