package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.UpdateUserRequest;
import com.arbitaja.refactored.backend.pam.core.port.in.user.UpdateUserUseCase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UpdateUserUseCase.UpdateUserCommand toUpdateUserCommand(Integer userId, UpdateUserRequest updateUserRequest);


}
