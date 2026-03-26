package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.SignupResponse;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SignupUserMapper {
    SignupResponse toSignupResponse(SignupUser signupUser);

    CreateUserUseCase.SignupCommand toSignupCommand(SignupRequest signupRequest);

    CreateUserUseCase.ApproveSignupCommand toApproveSignupCommand(Integer signupUserId, SignupRequest signupRequest);
}
