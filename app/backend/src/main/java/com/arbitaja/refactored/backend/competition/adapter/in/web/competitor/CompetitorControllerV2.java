package com.arbitaja.refactored.backend.competition.adapter.in.web.competitor;

import com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.request.CompetitorUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.competitor.dto.response.CompetitorResponse;
import com.arbitaja.refactored.backend.competition.adapter.in.web.annotations.RequiresCompetitionPermission;
import com.arbitaja.refactored.backend.competition.adapter.util.CompetitorWebMapper;
import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import com.arbitaja.refactored.backend.competition.core.port.in.competitor.GetCompetitorUseCase;
import com.arbitaja.refactored.backend.competition.core.port.in.competitor.ManageCompetitorUseCase;
import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Inbound REST adapter for competitor create/edit operations.
 */
@RestController
@RequestMapping("/v2/competitor")
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class CompetitorControllerV2 {

    private final ManageCompetitorUseCase manageCompetitorUseCase;
    private final GetCompetitorUseCase getCompetitorUseCase;
    private final CompetitorWebMapper mapper;

    @PostMapping
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_COMPETITORS)
    public ResponseEntity<CompetitorResponse> createCompetitor(@RequestBody @Valid CompetitorUpsertRequest request) {
        log.info("Creating competitor: {}", request.alias());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(manageCompetitorUseCase.createCompetitor(mapper.toCommand(request))));
    }

    @PutMapping("/{id}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_COMPETITORS)
    public ResponseEntity<CompetitorResponse> editCompetitor(
        @PathVariable Integer id,
        @RequestBody @Valid CompetitorUpsertRequest request
    ) {
        log.info("Editing competitor id: {}", id);
        return ResponseEntity.ok(mapper.toResponse(manageCompetitorUseCase.updateCompetitor(id, mapper.toCommand(request))));
    }

    @GetMapping("/{id}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.VIEW_COMPETITORS)
    public ResponseEntity<CompetitorResponse> getCompetitor(@PathVariable Integer id) {
        log.info("Getting competitor id: {}", id);
        return ResponseEntity.ok(mapper.toResponse(getCompetitorUseCase.getCompetitorById(id)));
    }

    @GetMapping
    @RequiresCompetitionPermission(CompetitionPermissionCode.VIEW_COMPETITORS)
    public ResponseEntity<Set<CompetitorResponse>> getAllCompetitors() {
        log.info("Getting all competitors");
        return ResponseEntity.ok(getCompetitorUseCase.getAllCompetitors().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toSet()));
    }

    @GetMapping("/competition/{competitionId}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.VIEW_COMPETITORS)
    public ResponseEntity<Set<CompetitorResponse>> getCompetitorsByCompetitionId(@PathVariable Integer competitionId) {
        log.info("Getting competitors for competition id: {}", competitionId);
        return ResponseEntity.ok(
            getCompetitorUseCase.getCompetitorsByCompetitionId(competitionId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toSet()));
    }
}

