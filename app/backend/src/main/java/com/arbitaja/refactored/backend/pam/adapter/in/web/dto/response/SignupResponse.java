package com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupResponse {

    long userId;
    String username;
    String email;
    Integer schoolId;
    String message;
}
