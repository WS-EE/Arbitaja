package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO for user update requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateUserRequest {

    @NonNull
    private String username;

    @NonNull
    @JsonProperty("full_name")
    private String fullName;

    @NonNull
    private String email;

    @JsonProperty("school_id")
    private Integer schoolId;
}

