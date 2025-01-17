package com.arbitaja.backend.users.APIs.responses;

import com.arbitaja.backend.users.dataobjects.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

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

    public UserProfileResponse(int id, String username, List<Role> roles, Set<SimpleGrantedAuthority> permissions) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Set<SimpleGrantedAuthority> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<SimpleGrantedAuthority> permissions) {
        this.permissions = permissions;
    }

    public PersonalDataResponse getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalDataResponse personalData) {
        this.personalData = personalData;
    }

    public static class PersonalDataResponse {
        @Schema(description = "Full name of the user", example = "John Doe")
        @JsonProperty("full_name")
        private String fullName;

        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        private String email;

        @Schema(description = "School details of the user")
        private SchoolResponse school;

        public PersonalDataResponse() {
            this.fullName = "";
            this.email = "";
            this.school = new SchoolResponse();
        }

        public PersonalDataResponse(String fullName, String email, SchoolResponse school) {
            this.fullName = fullName;
            this.email = email;
            this.school = school;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public SchoolResponse getSchool() {
            return school;
        }

        public void setSchool(SchoolResponse school) {
            this.school = school;
        }
    }

    public static class SchoolResponse {
        @Schema(description = "School ID", example = "101")
        private Object id;

        @Schema(description = "School name", example = "Springfield High School")
        private String name;

        public SchoolResponse() {
            this.id = "";
            this.name = "";
        }

        public SchoolResponse(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Object getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}