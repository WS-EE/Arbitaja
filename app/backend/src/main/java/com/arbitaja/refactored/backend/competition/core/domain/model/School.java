package com.arbitaja.refactored.backend.competition.core.domain.model;

import lombok.*;

/**
 * Core domain model for school management.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class School {

    private Integer id;
    private String name;
}

