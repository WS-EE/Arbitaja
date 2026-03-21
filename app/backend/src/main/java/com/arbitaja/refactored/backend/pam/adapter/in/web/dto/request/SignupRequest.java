package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NonNull
    @JsonProperty("full_name")
    @Size(min = 1, max = 100)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @JsonProperty("school_id")
    private Integer schoolId;
}

