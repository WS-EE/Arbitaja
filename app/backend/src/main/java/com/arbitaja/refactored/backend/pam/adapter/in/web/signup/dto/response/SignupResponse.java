package com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {

    long userId;
    String username;
    String email;
    String fullName;
    Integer schoolId;
}

