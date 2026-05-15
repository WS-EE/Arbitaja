package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.ApproveSignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.request.SignupRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.signup.dto.response.SignupResponse;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SignupUserMapper {
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "email", source = "personalData.email")
    @Mapping(target = "schoolId", source = "personalData.school.id")
    @Mapping(target = "fullName", source = "personalData.fullName")
    @Mapping(target = "createdAt", source = "createdAt")
    SignupResponse toSignupResponse(SignupUser signupUser);

    CreateUserUseCase.SignupCommand toSignupCommand(SignupRequest signupRequest);

    CreateUserUseCase.ApproveSignupCommand toApproveSignupCommand(Integer signupUserId, ApproveSignupRequest request);
}
