package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.response.SignupResponse;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignupUserMapperTest {

    private final SignupUserMapper signupUserMapper = Mappers.getMapper(SignupUserMapper.class);

    @Test
    void toSignupCommandMapsPasswordAndOtherFields() {
        SignupRequest request = SignupRequest.builder()
            .username("random")
            .password("random")
            .fullName("Random User")
            .email("random@random.com")
            .schoolId(1)
            .build();

        CreateUserUseCase.SignupCommand command = signupUserMapper.toSignupCommand(request);

        assertEquals("random", command.getUsername());
        assertEquals("random", command.getPassword());
        assertEquals("Random User", command.getFullName());
        assertEquals("random@random.com", command.getEmail());
        assertEquals(1, command.getSchoolId());
    }

    @Test
    void toSignupResponseMapsUserIdEmailAndSchoolId() {
        SignupUser signupUser = SignupUser.builder()
            .id(44)
            .username("random")
            .saltedPassword("ignored")
            .personalData(PersonalData.builder()
                .fullName("Random User")
                .email("random@random.com")
                .school(School.builder().id(7).name("School").build())
                .build())
            .build();

        SignupResponse response = signupUserMapper.toSignupResponse(signupUser);

        assertEquals(44L, response.getUserId());
        assertEquals("random", response.getUsername());
        assertEquals("random@random.com", response.getEmail());
        assertEquals(7, response.getSchoolId());
    }

    @Test
    void toApproveSignupCommandMapsSignupIdAndRequestFields() {
        SignupRequest request = SignupRequest.builder()
            .username("approved")
            .password("ignored")
            .fullName("Approved User")
            .email("approved@example.com")
            .schoolId(3)
            .build();

        CreateUserUseCase.ApproveSignupCommand command = signupUserMapper.toApproveSignupCommand(99, request);

        assertEquals(99, command.getSignupUserId());
        assertEquals("approved", command.getUsername());
        assertEquals("Approved User", command.getFullName());
        assertEquals("approved@example.com", command.getEmail());
        assertEquals(3, command.getSchoolId());
    }
}


