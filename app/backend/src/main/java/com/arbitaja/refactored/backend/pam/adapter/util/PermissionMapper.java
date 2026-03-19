package com.arbitaja.refactored.backend.pam.adapter.util;

import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.request.CreatePermissionRequest;
import com.arbitaja.refactored.backend.pam.adapter.in.web.dto.response.PermissionResponse;
import com.arbitaja.refactored.backend.pam.core.domain.model.Permission;
import com.arbitaja.refactored.backend.pam.core.port.in.permission.CreatePermissionUseCase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toPermissionResponse(Permission permission);

    CreatePermissionUseCase.PermissionCommand toPermissionCommand(CreatePermissionRequest request);
}