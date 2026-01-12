package com.arbitaja.refactored.backend.pam.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO for signup requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SignupRequest {

    @NonNull
    private String username;

    @NonNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NonNull
    @JsonProperty("full_name")
    private String fullName;

    @NonNull
    private String email;

    @JsonProperty("school_id")
    private Integer schoolId;
}

