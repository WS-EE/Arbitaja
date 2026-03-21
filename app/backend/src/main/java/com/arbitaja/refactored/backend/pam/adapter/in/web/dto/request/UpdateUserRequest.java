package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
    private String username;

    @JsonProperty("full_name")
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @JsonProperty("school_id")
    private Integer schoolId;
}

