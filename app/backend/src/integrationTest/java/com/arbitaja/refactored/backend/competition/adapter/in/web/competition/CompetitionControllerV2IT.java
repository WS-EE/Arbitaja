package com.arbitaja.refactored.backend.competition.adapter.in.web.competition;

import com.arbitaja.refactored.backend.competition.adapter.in.web.competition.dto.response.CompetitionResponse;
import com.arbitaja.refactored.backend.competition.adapter.util.CompetitionWebMapper;
import com.arbitaja.refactored.backend.competition.core.domain.model.Competition;
import com.arbitaja.refactored.backend.competition.core.port.in.competition.GetCompetitionUseCase;
import com.arbitaja.refactored.backend.competition.core.port.in.competition.ManageCompetitionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompetitionControllerV2IT {

    @Mock
    private GetCompetitionUseCase getCompetitionUseCase;

    @Mock
    private ManageCompetitionUseCase manageCompetitionUseCase;

    @Mock
    private CompetitionWebMapper mapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CompetitionControllerV2 controller = new CompetitionControllerV2(
            getCompetitionUseCase, manageCompetitionUseCase, mapper
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void getAllCompetitionsReturnsPagedContent() throws Exception {
        Competition competition1 = Competition.builder().id(1).name("Alpha").build();
        Competition competition2 = Competition.builder().id(2).name("Beta").build();
        CompetitionResponse response1 = new CompetitionResponse(1, "Alpha", null, null, null, null, null, Set.of());
        CompetitionResponse response2 = new CompetitionResponse(2, "Beta", null, null, null, null, null, Set.of());
        Pageable pageable = PageRequest.of(0, 20);

        when(getCompetitionUseCase.getCompetitionsPaged(eq(""), eq(""), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(competition1, competition2), pageable, 2));
        when(mapper.toResponse(competition1)).thenReturn(response1);
        when(mapper.toResponse(competition2)).thenReturn(response2);

        mockMvc.perform(get("/v2/competition"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Alpha"))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getAllCompetitionsWithSearchFiltersResults() throws Exception {
        Competition competition = Competition.builder().id(3).name("Alpha Open").build();
        CompetitionResponse response = new CompetitionResponse(3, "Alpha Open", null, null, null, null, null, Set.of());
        Pageable pageable = PageRequest.of(0, 20);

        when(getCompetitionUseCase.getCompetitionsPaged(eq("Alpha"), eq(""), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(competition), pageable, 1));
        when(mapper.toResponse(competition)).thenReturn(response);

        mockMvc.perform(get("/v2/competition").param("search", "Alpha"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Alpha Open"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllCompetitionsWithStatusFilterPassesStatus() throws Exception {
        Competition competition = Competition.builder().id(4).name("Live Event").build();
        CompetitionResponse response = new CompetitionResponse(4, "Live Event", null, null, null, null, null, Set.of());
        Pageable pageable = PageRequest.of(0, 20);

        when(getCompetitionUseCase.getCompetitionsPaged(eq(""), eq("ONGOING"), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(competition), pageable, 1));
        when(mapper.toResponse(competition)).thenReturn(response);

        mockMvc.perform(get("/v2/competition").param("status", "ONGOING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Live Event"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllCompetitionsReturnsEmptyPageWhenNoResults() throws Exception {
        when(getCompetitionUseCase.getCompetitionsPaged(eq("nonexistent"), eq(""), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(get("/v2/competition").param("search", "nonexistent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.totalElements").value(0));
    }
}
