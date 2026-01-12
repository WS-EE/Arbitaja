package com.arbitaja.refactored.backend.pam.adapter.in.web.dto;

import com.arbitaja.backend.users.dataobjects.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for user profile data")
public class UserProfileResponse {

    @Schema(description = "User ID", example = "12345")
    private int id;

    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @Schema(description = "List of user roles")
    private List<Role> roles;

    @Schema(description = "Set of user permissions")
    private Set<SimpleGrantedAuthority> permissions;

    @Schema(description = "Personal data of the user")
    @JsonProperty("personal_data")
    private PersonalDataResponse personalData;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalDataResponse {
        @Schema(description = "Personal data id", example = "1")
        @JsonProperty("id")
        private Integer id;

        @Schema(description = "Full name of the user", example = "John Doe")
        @JsonProperty("full_name")
        private String fullName;

        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        private String email;

        @Schema(description = "School details of the user")
        private SchoolResponse school;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchoolResponse {
        @Schema(description = "School ID", example = "101")
        private Object id;

        @Schema(description = "School name", example = "Springfield High School")
        private String name;
    }
}