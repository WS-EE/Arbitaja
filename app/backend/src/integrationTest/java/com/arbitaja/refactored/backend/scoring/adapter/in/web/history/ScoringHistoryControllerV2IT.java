package com.arbitaja.refactored.backend.scoring.adapter.in.web.history;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.ScoringExceptionHandler;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.request.AddScoringHistoryRequest;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.history.dto.response.ScoringHistoryEntryResponse;
import com.arbitaja.refactored.backend.scoring.adapter.util.ScoringHistoryWebMapper;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.ValidationException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringHistoryEntry;
import com.arbitaja.refactored.backend.scoring.core.port.in.history.RecordScoringHistoryUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ScoringHistoryControllerV2IT {

    @Mock
    private RecordScoringHistoryUseCase recordScoringHistoryUseCase;

    @Mock
    private ScoringHistoryWebMapper mapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ScoringHistoryControllerV2 controller = new ScoringHistoryControllerV2(recordScoringHistoryUseCase, mapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ScoringExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void recordScoreReturnsMappedResponse() throws Exception {
        AddScoringHistoryRequest request = new AddScoringHistoryRequest(1, 2, 3, 5.0);
        RecordScoringHistoryUseCase.RecordScoringCommand command =
            RecordScoringHistoryUseCase.RecordScoringCommand.builder()
                .competitionId(1).competitorId(2).scoringCriterionId(3).points(5.0).build();
        Timestamp now = Timestamp.valueOf("2026-04-06 12:00:00");
        ScoringHistoryEntry entry = ScoringHistoryEntry.builder()
            .id(99).competitionId(1).competitorId(2).scoringCriterionId(3)
            .scoringCriterionName("ssh").pointsGiven(5.0).createdAt(now).build();
        ScoringHistoryEntryResponse response = new ScoringHistoryEntryResponse(99, 1, 2, 3, "ssh", 5.0, now);

        when(mapper.toCommand(any(AddScoringHistoryRequest.class))).thenReturn(command);
        when(recordScoringHistoryUseCase.recordScore(command)).thenReturn(entry);
        when(mapper.toResponse(entry)).thenReturn(response);

        mockMvc.perform(post("/v2/scoring/history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(99))
            .andExpect(jsonPath("$.points_given").value(5.0));
    }

    @Test
    void recordScoreReturns400OnValidationFailure() throws Exception {
        AddScoringHistoryRequest request = new AddScoringHistoryRequest(1, 2, 3, 100.0);
        RecordScoringHistoryUseCase.RecordScoringCommand command =
            RecordScoringHistoryUseCase.RecordScoringCommand.builder()
                .competitionId(1).competitorId(2).scoringCriterionId(3).points(100.0).build();

        when(mapper.toCommand(any(AddScoringHistoryRequest.class))).thenReturn(command);
        when(recordScoringHistoryUseCase.recordScore(command))
            .thenThrow(ValidationException.pointsOutOfRange(10.0));

        mockMvc.perform(post("/v2/scoring/history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid input"));
    }

    @Test
    void recordScoreReturns404WhenCompetitionMissing() throws Exception {
        AddScoringHistoryRequest request = new AddScoringHistoryRequest(99, 2, 3, 5.0);
        RecordScoringHistoryUseCase.RecordScoringCommand command =
            RecordScoringHistoryUseCase.RecordScoringCommand.builder()
                .competitionId(99).competitorId(2).scoringCriterionId(3).points(5.0).build();

        when(mapper.toCommand(any(AddScoringHistoryRequest.class))).thenReturn(command);
        when(recordScoringHistoryUseCase.recordScore(command))
            .thenThrow(EntityNotFoundException.competition(99));

        mockMvc.perform(post("/v2/scoring/history")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

}
