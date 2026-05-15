package com.arbitaja.refactored.backend.competition.adapter.in.web.school;

import com.arbitaja.refactored.backend.competition.adapter.in.web.annotations.RequiresCompetitionPermission;
import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.request.SchoolUpsertRequest;
import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.response.GeneralMessageResponse;
import com.arbitaja.refactored.backend.competition.adapter.in.web.school.dto.response.SchoolResponse;
import com.arbitaja.refactored.backend.competition.adapter.util.SchoolWebMapper;
import com.arbitaja.refactored.backend.competition.core.domain.enums.CompetitionPermissionCode;
import com.arbitaja.refactored.backend.competition.core.port.in.school.GetSchoolUseCase;
import com.arbitaja.refactored.backend.competition.core.port.in.school.ManageSchoolUseCase;
import com.arbitaja.refactored.backend.common.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Inbound REST adapter for school management use cases.
 */
@RestController
@RequestMapping("/v2/schools")
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "arbitaja.mode", havingValue = "hex")
public class SchoolControllerV2 {

    private final GetSchoolUseCase getSchoolUseCase;
    private final ManageSchoolUseCase manageSchoolUseCase;
    private final SchoolWebMapper mapper;

    @GetMapping
    @RequiresCompetitionPermission(CompetitionPermissionCode.VIEW_SCHOOLS)
    public ResponseEntity<PagedResponse<SchoolResponse>> getAllSchools(
        @RequestParam(required = false, defaultValue = "") String search,
        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("Getting schools paged, search={}", search);
        return ResponseEntity.ok(PagedResponse.from(
            getSchoolUseCase.getSchoolsPaged(search, pageable)
                .map(mapper::toResponse)
        ));
    }

    @GetMapping("/{id}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.VIEW_SCHOOLS)
    public ResponseEntity<SchoolResponse> getSchoolById(@PathVariable Integer id) {
        log.info("Getting school by id: {}", id);
        return ResponseEntity.ok(mapper.toResponse(getSchoolUseCase.getSchoolById(id)));
    }

    @PostMapping
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_SCHOOLS)
    public ResponseEntity<SchoolResponse> createSchool(@RequestBody @Valid SchoolUpsertRequest request) {
        log.info("Creating school: {}", request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponse(manageSchoolUseCase.createSchool(mapper.toCommand(request))));
    }

    @PutMapping("/{id}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_SCHOOLS)
    public ResponseEntity<SchoolResponse> updateSchool(
        @PathVariable Integer id,
        @RequestBody @Valid SchoolUpsertRequest request
    ) {
        log.info("Updating school id: {}", id);
        return ResponseEntity.ok(mapper.toResponse(manageSchoolUseCase.updateSchool(id, mapper.toCommand(request))));
    }

    @DeleteMapping("/{id}")
    @RequiresCompetitionPermission(CompetitionPermissionCode.CREATE_UPDATE_SCHOOLS)
    public ResponseEntity<GeneralMessageResponse> deleteSchool(@PathVariable Integer id) {
        log.info("Deleting school id: {}", id);
        manageSchoolUseCase.deleteSchool(id);
        return ResponseEntity.ok(new GeneralMessageResponse("School deleted successfully"));
    }
}

