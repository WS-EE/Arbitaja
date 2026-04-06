package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.request.CreatePermissionRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.permission.dto.response.PermissionResponse;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CreatePermissionUseCase;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionMapperTest {

    private final PermissionMapper mapper = Mappers.getMapper(PermissionMapper.class);

    @Test
    void toPermissionResponseMapsAllFields() {
        Permission permission = Permission.builder().id(9).name("View users").key("VIEW_USERS").build();

        PermissionResponse response = mapper.toPermissionResponse(permission);

        assertEquals(9, response.id());
        assertEquals("View users", response.name());
        assertEquals("VIEW_USERS", response.key());
    }

    @Test
    void toPermissionCommandMapsAllFields() {
        CreatePermissionRequest request = new CreatePermissionRequest("Edit users", "EDIT_USERS");

        CreatePermissionUseCase.PermissionCommand command = mapper.toPermissionCommand(request);

        assertEquals("Edit users", command.name());
        assertEquals("EDIT_USERS", command.key());
    }
}

