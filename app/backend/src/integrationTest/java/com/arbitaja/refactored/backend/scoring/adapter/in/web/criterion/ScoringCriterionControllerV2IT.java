package com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion;

import com.arbitaja.refactored.backend.scoring.adapter.in.web.ScoringExceptionHandler;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.request.ScoringCriterionUpsertRequest;
import com.arbitaja.refactored.backend.scoring.adapter.in.web.criterion.dto.response.ScoringCriterionResponse;
import com.arbitaja.refactored.backend.scoring.adapter.util.ScoringCriterionWebMapper;
import com.arbitaja.refactored.backend.scoring.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.scoring.core.domain.model.ScoringCriterion;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.GetScoringCriterionUseCase;
import com.arbitaja.refactored.backend.scoring.core.port.in.criterion.ManageScoringCriterionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ScoringCriterionControllerV2IT {

    @Mock
    private GetScoringCriterionUseCase getScoringCriterionUseCase;

    @Mock
    private ManageScoringCriterionUseCase manageScoringCriterionUseCase;

    @Mock
    private ScoringCriterionWebMapper mapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ScoringCriterionControllerV2 controller = new ScoringCriterionControllerV2(
            getScoringCriterionUseCase, manageScoringCriterionUseCase, mapper
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new ScoringExceptionHandler())
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllReturnsPagedContent() throws Exception {
        ScoringCriterion criterion1 = ScoringCriterion.builder().id(1).name("speed").totalPoints(10.0).build();
        ScoringCriterion criterion2 = ScoringCriterion.builder().id(2).name("accuracy").totalPoints(5.0).build();
        ScoringCriterionResponse response1 = new ScoringCriterionResponse(
            1, "speed", null, null, 10.0, null, null, null, null, null, null
        );
        ScoringCriterionResponse response2 = new ScoringCriterionResponse(
            2, "accuracy", null, null, 5.0, null, null, null, null, null, null
        );
        Pageable pageable = PageRequest.of(0, 20);

        when(getScoringCriterionUseCase.getScoringCriteriaPaged(eq(""), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(criterion1, criterion2), pageable, 2));
        when(mapper.toResponse(criterion1)).thenReturn(response1);
        when(mapper.toResponse(criterion2)).thenReturn(response2);

        mockMvc.perform(get("/v2/scoring/criteria"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("speed"))
            .andExpect(jsonPath("$.content[0].total_points").value(10.0))
            .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAllPagedWithSearchFiltersResults() throws Exception {
        ScoringCriterion criterion = ScoringCriterion.builder().id(1).name("speed").totalPoints(10.0).build();
        ScoringCriterionResponse response = new ScoringCriterionResponse(
            1, "speed", null, null, 10.0, null, null, null, null, null, null
        );
        Pageable pageable = PageRequest.of(0, 20);

        when(getScoringCriterionUseCase.getScoringCriteriaPaged(eq("speed"), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(criterion), pageable, 1));
        when(mapper.toResponse(criterion)).thenReturn(response);

        mockMvc.perform(get("/v2/scoring/criteria").param("search", "speed"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("speed"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getByIdReturnsMappedResponse() throws Exception {
        ScoringCriterion criterion = ScoringCriterion.builder().id(7).name("ssh").totalPoints(5.0).build();
        ScoringCriterionResponse response = new ScoringCriterionResponse(
            7, "ssh", null, null, 5.0, null, null, null, null, null, null
        );
        when(getScoringCriterionUseCase.getScoringCriterionById(7)).thenReturn(criterion);
        when(mapper.toResponse(criterion)).thenReturn(response);

        mockMvc.perform(get("/v2/scoring/criteria/7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.name").value("ssh"));
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        when(getScoringCriterionUseCase.getScoringCriterionById(404))
            .thenThrow(EntityNotFoundException.scoringCriterion(404));

        mockMvc.perform(get("/v2/scoring/criteria/404"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Object not found"));
    }

    @Test
    void getByCompetitionReturnsMappedCollection() throws Exception {
        ScoringCriterion criterion = ScoringCriterion.builder().id(7).name("ssh").totalPoints(5.0).build();
        ScoringCriterionResponse response = new ScoringCriterionResponse(
            7, "ssh", null, null, 5.0, null, null, null, null, null, null
        );
        when(getScoringCriterionUseCase.getScoringCriteriaForCompetition(1)).thenReturn(List.of(criterion));
        when(mapper.toResponse(criterion)).thenReturn(response);

        mockMvc.perform(get("/v2/scoring/criteria/by-competition/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(7));
    }

    @Test
    void createReturnsMappedResponse() throws Exception {
        ScoringCriterionUpsertRequest request = new ScoringCriterionUpsertRequest(
            "speed", null, true, 10.0, null, null, null, null, null, null, null
        );
        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command =
            ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
                .name("speed").manual(true).totalPoints(10.0).build();
        ScoringCriterion saved = ScoringCriterion.builder().id(11).name("speed").manual(true).totalPoints(10.0).build();
        ScoringCriterionResponse response = new ScoringCriterionResponse(
            11, "speed", null, true, 10.0, null, null, null, null, null, null
        );

        when(mapper.toCommand(any(ScoringCriterionUpsertRequest.class))).thenReturn(command);
        when(manageScoringCriterionUseCase.createScoringCriterion(command)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        mockMvc.perform(post("/v2/scoring/criteria")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(11));
    }

    @Test
    void updateMapsEntityNotFoundTo404() throws Exception {
        ScoringCriterionUpsertRequest request = new ScoringCriterionUpsertRequest(
            "speed", null, null, 10.0, null, null, null, null, null, null, null
        );
        ManageScoringCriterionUseCase.UpsertScoringCriterionCommand command =
            ManageScoringCriterionUseCase.UpsertScoringCriterionCommand.builder()
                .name("speed").totalPoints(10.0).build();

        when(mapper.toCommand(any(ScoringCriterionUpsertRequest.class))).thenReturn(command);
        when(manageScoringCriterionUseCase.updateScoringCriterion(anyInt(), any()))
            .thenThrow(EntityNotFoundException.scoringCriterion(404));

        mockMvc.perform(put("/v2/scoring/criteria/404")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Object not found"));
    }

    @Test
    void deleteReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/v2/scoring/criteria/7"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Scoring criterion deleted successfully"));

        verify(manageScoringCriterionUseCase).deleteScoringCriterion(7);
    }

    @Test
    void deleteMapsEntityNotFoundTo404() throws Exception {
        doThrow(EntityNotFoundException.scoringCriterion(404))
            .when(manageScoringCriterionUseCase).deleteScoringCriterion(404);

        mockMvc.perform(delete("/v2/scoring/criteria/404"))
            .andExpect(status().isNotFound());
    }

    @Test
    void linkToCompetitionReturnsSuccess() throws Exception {
        mockMvc.perform(post("/v2/scoring/criteria/7/competitions/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Scoring criterion added to competition successfully"));

        verify(manageScoringCriterionUseCase).addScoringCriterionToCompetition(1, 7);
    }
}
