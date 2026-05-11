package com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.annotations.RequiresScoringPermission;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.request.ScoringCriterionUpsertRequest;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.response.ScoringCriterionResponse;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.shared.dto.response.GeneralMessageResponse;
import com.arbitaja.refactored.backend.scoring.adapter.util.ScoringCriterionWebMapper;
import com.arbitaja.refactored.backend.scoring.core.domain.enums.ScoringPermissionCode;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.GetScoringCriterionUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.ManageScoringCriterionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Inbound REST adapter for scoring criterion management use cases.
 */
@RestController
@RequestMapping("/v2/scoring/criteria")
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex", matchIfMissing = true)
public class ScoringCriterionControllerV2 {

    private final GetScoringCriterionUseCase getScoringCriterionUseCase;
    private final ManageScoringCriterionUseCase manageScoringCriterionUseCase;
    private final ScoringCriterionWebMapper mapper;

    @GetMapping
    @RequiresScoringPermission(ScoringPermissionCode.MANAGE_SCORING_CRITERIA)
    public ResponseEntity<List<ScoringCriterionResponse>> getAll() {
        log.info("Getting all scoring criteria");
        return ResponseEntity.ok(getScoringCriterionUseCase.getAllScoringCriteria().stream()
            .map(mapper::toResponse)
            .toList());
    }

    @GetMapping("/{id}")
    @RequiresScoringPermission(ScoringPermissionCode.MANAGE_SCORING_CRITERIA)
    public ResponseEntity<ScoringCriterionResponse> getById(@PathVariable Integer id) {
        log.info("Getting scoring criterion {}", id);
        return ResponseEntity.ok(mapper.toResponse(getScoringCriterionUseCase.getScoringCriterionById(id)));
    }

    @GetMapping("/by-competition/{competitionId}")
    @RequiresScoringPermission(ScoringPermissionCode.MANAGE_SCORING_CRITERIA)
    public ResponseEntity<List<ScoringCriterionResponse>> getByCompetition(@PathVariable Integer competitionId) {
        log.info("Getting scoring criteria for competition {}", competitionId);
        return ResponseEntity.ok(getScoringCriterionUseCase.getScoringCriteriaForCompetition(competitionId).stream()
            .map(mapper::toResponse)
            .toList());
    }

    @PostMapping
    @RequiresScoringPermission(ScoringPermissionCode.MANAGE_SCORING_CRITERIA)
    public ResponseEntity<ScoringCriterionResponse> create(@RequestBody @Valid ScoringCriterionUpsertRequest request) {
        log.info("Creating scoring criterion: {}", request.name());
        return ResponseEntity.status(201).body(
            mapper.toResponse(manageScoringCriterionUseCase.createScoringCriterion(mapper.toCommand(request)))
        );
    }

    @PutMapping("/{id}")
    @RequiresScoringPermission(ScoringPermissionCode.MANAGE_SCORING_CRITERIA)
    public ResponseEntity<ScoringCriterionResponse> update(
        @PathVariable Integer id,
        @RequestBody @Valid ScoringCriterionUpsertRequest request
    ) {
        log.info("Updating scoring criterion {}", id);
        return ResponseEntity.ok(
            mapper.toResponse(manageScoringCriterionUseCase.updateScoringCriterion(id, mapper.toCommand(request)))
        );
    }

    @DeleteMapping("/{id}")
    @RequiresScoringPermission(ScoringPermissionCode.MANAGE_SCORING_CRITERIA)
    public ResponseEntity<GeneralMessageResponse> delete(@PathVariable Integer id) {
        log.info("Deleting scoring criterion {}", id);
        manageScoringCriterionUseCase.deleteScoringCriterion(id);
        return ResponseEntity.ok(new GeneralMessageResponse("Scoring criterion deleted successfully"));
    }

    @PostMapping("/{criterionId}/competitions/{competitionId}")
    @RequiresScoringPermission(ScoringPermissionCode.MANAGE_SCORING_CRITERIA)
    public ResponseEntity<GeneralMessageResponse> linkToCompetition(
        @PathVariable Integer criterionId,
        @PathVariable Integer competitionId
    ) {
        log.info("Linking scoring criterion {} to competition {}", criterionId, competitionId);
        manageScoringCriterionUseCase.addScoringCriterionToCompetition(competitionId, criterionId);
        return ResponseEntity.ok(new GeneralMessageResponse("Scoring criterion added to competition successfully"));
    }
}
