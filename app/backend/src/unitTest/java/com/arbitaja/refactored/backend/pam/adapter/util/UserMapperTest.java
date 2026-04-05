package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.user.dto.request.UpdateUserRequest;
import com.arbitaja.refactored.backend.pam.core.port.in.user.UpdateUserUseCase;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUpdateUserCommandMapsPathAndBodyFields() {
        UpdateUserRequest request = UpdateUserRequest.builder()
            .username("new-user")
            .fullName("New User")
            .email("new@example.com")
            .schoolId(4)
            .build();

        UpdateUserUseCase.UpdateUserCommand command = mapper.toUpdateUserCommand(55, request);

        assertEquals(55, command.getUserId());
        assertEquals("new-user", command.getUsername());
        assertEquals("New User", command.getFullName());
        assertEquals("new@example.com", command.getEmail());
        assertEquals(4, command.getSchoolId());
    }
}

