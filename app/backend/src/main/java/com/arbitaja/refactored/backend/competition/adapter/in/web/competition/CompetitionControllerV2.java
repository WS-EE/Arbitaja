package com.arbitaja.refactored.backend.competition.adapter.in.web.competition;

import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.request.CompetitionUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.request.OverwriteCompetitionCompetitorsRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.response.CompetitionResponse;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.response.GeneralMessageResponse;
import com.arbitaja.refactored.backend.competition.adapter.in.web.annotations.RequiresCompetitionPermission;
import com.arbitaja.refactored.backend.competition.adapter.util.CompetitionWebMapper;
import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import com.arbitaja.refactored.backend.competition.core.port.in.competition.GetCompetitionUseCase;
import com.arbitaja.refactored.backend.competition.core.port.in.competition.ManageCompetitionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Inbound REST adapter for competition management use cases.
 */
@RestController
@RequestMapping("/v2/competition")
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "arbitaja.competition.mode", havingValue = "hex")
public class CompetitionControllerV2 {

    private final GetCompetitionUseCase getCompetitionUseCase;
    private final ManageCompetitionUseCase manageCompetitionUseCase;
    private final CompetitionWebMapper mapper;

    @GetMapping
    @RequiresCompetitionPermission(CompetitionPermissionCode.VIEW_COMPETITIONS)
    public ResponseEntity<List<CompetitionResponse>> getAllCompetitions() {
        log.info("Getting all competitions");
        List<CompetitionResponse> competitions = getCompetitionUseCase.getAllCompetitions().stream()
            .map(mapper::toResponse)
            .toList();
        return ResponseEntity.ok(competitions);
    }

    @GetMapping("/{id}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.VIEW_COMPETITIONS)
    public ResponseEntity<CompetitionResponse> getCompetitionById(@PathVariable Integer id) {
        log.info("Getting competition by id: {}", id);
        return ResponseEntity.ok(mapper.toResponse(getCompetitionUseCase.getCompetitionById(id)));
    }

    @GetMapping("/by-name")
    @RequiresCompetitionPermission(CompetitionPermissionCode.VIEW_COMPETITIONS)
    public ResponseEntity<CompetitionResponse> getCompetitionByName(@RequestParam String name) {
        log.info("Getting competition by name: {}", name);
        return ResponseEntity.ok(mapper.toResponse(getCompetitionUseCase.getCompetitionByName(name)));
    }

    @PostMapping
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS)
    public ResponseEntity<CompetitionResponse> createCompetition(@RequestBody @Valid CompetitionUpsertRequest request) {
        log.info("Creating competition: {}", request.name());
        return ResponseEntity.status(201).body(
            mapper.toResponse(manageCompetitionUseCase.createCompetition(mapper.toCommand(request)))
        );
    }

    @PutMapping("/{id}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS)
    public ResponseEntity<CompetitionResponse> updateCompetition(
        @PathVariable Integer id,
        @RequestBody @Valid CompetitionUpsertRequest request
    ) {
        log.info("Updating competition id: {}", id);
        return ResponseEntity.ok(
            mapper.toResponse(manageCompetitionUseCase.updateCompetition(id, mapper.toCommand(request)))
        );
    }

    @PostMapping("/{competitionId}/competitors/{competitorId}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS)
    public ResponseEntity<CompetitionResponse> addCompetitorToCompetition(
        @PathVariable Integer competitionId,
        @PathVariable Integer competitorId
    ) {
        log.info("Adding competitor {} to competition {}", competitorId, competitionId);
        return ResponseEntity.ok(
            mapper.toResponse(manageCompetitionUseCase.addCompetitorToCompetition(competitionId, competitorId))
        );
    }

    @DeleteMapping("/{competitionId}/competitors/{competitorId}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS)
    public ResponseEntity<CompetitionResponse> removeCompetitorFromCompetition(
        @PathVariable Integer competitionId,
        @PathVariable Integer competitorId
    ) {
        log.info("Removing competitor {} from competition {}", competitorId, competitionId);
        return ResponseEntity.ok(
            mapper.toResponse(manageCompetitionUseCase.removeCompetitorFromCompetition(competitionId, competitorId))
        );
    }

    @PutMapping("/{competitionId}/competitors")
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS)
    public ResponseEntity<CompetitionResponse> overwriteCompetitionCompetitors(
        @PathVariable Integer competitionId,
        @RequestBody OverwriteCompetitionCompetitorsRequest request
    ) {
        log.info("Overwriting competitors {} for competition {}", request.competitorIds(), competitionId);
        return ResponseEntity.ok(
            mapper.toResponse(manageCompetitionUseCase.overwriteCompetitionCompetitors(competitionId, request.competitorIds()))
        );
    }

    @DeleteMapping("/{id}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_COMPETITIONS)
    public ResponseEntity<GeneralMessageResponse> deleteCompetition(@PathVariable Integer id) {
        log.info("Deleting competition id: {}", id);
        manageCompetitionUseCase.deleteCompetition(id);
        return ResponseEntity.ok(new GeneralMessageResponse("Competition deleted successfully"));
    }
}

