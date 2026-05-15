package com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for approving a signup request. All fields are optional overrides applied during approval.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApproveSignupRequest {

    private String username;

    @JsonProperty("full_name")
    @Size(min = 1, max = 100)
    private String fullName;

    @Email
    @Size(max = 254)
    private String email;

    @JsonProperty("school_id")
    private Integer schoolId;
}
