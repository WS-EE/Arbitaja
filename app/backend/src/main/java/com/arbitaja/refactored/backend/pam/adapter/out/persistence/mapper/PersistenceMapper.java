package com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.*;
import com.arbitaja.refactored.backend.pam.core.domain.model.*;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between domain models and JPA entities.
 */
@Component
public class PersistenceMapper {

    // User mappings
    public User toDomain(@NonNull UserJpaEntity entity) {
        User user = User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .saltedPassword(entity.getSaltedPassword())
                .personalData(entity.getPersonalData() != null ? toDomain(entity.getPersonalData()) : null)
                .build();

        if (entity.getUserRoles() != null) {
            user.setUserRoles(entity.getUserRoles().stream()
                    .map(ur -> toDomainUserRole(ur, user))
                    .collect(Collectors.toSet()));
        }

        return user;
    }

    public UserJpaEntity toEntity(@NonNull User domain) {
        return UserJpaEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .saltedPassword(domain.getSaltedPassword())
                .personalData(domain.getPersonalData() != null ? toEntity(domain.getPersonalData()) : null)
                .build();
    }

    // PersonalData mappings
    public PersonalData toDomain(@NonNull PersonalDataJpaEntity entity) {
        return PersonalData.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .school(entity.getSchool() != null ? toDomain(entity.getSchool()) : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public PersonalDataJpaEntity toEntity(@NonNull PersonalData domain) {
        return PersonalDataJpaEntity.builder()
                .id(domain.getId())
                .fullName(domain.getFullName())
                .email(domain.getEmail())
                .school(domain.getSchool() != null ? toEntity(domain.getSchool()) : null)
                .createdAt(domain.getCreatedAt())
                .build();
    }

    // School mappings
    public School toDomain(@NonNull SchoolJpaEntity entity) {
        return School.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public SchoolJpaEntity toEntity(@NonNull School domain) {
        return SchoolJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

    // Role mappings
    public Role toDomain(@NonNull RoleJpaEntity entity) {
        return toDomainRole(entity, new HashSet<>());
    }

    private Role toDomainRole(@NonNull RoleJpaEntity entity, Set<String> visitedRoleKeys) {
        String roleVisitKey = getRoleVisitKey(entity);
        if (!visitedRoleKeys.add(roleVisitKey)) {
            return Role.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .createdAt(entity.getCreatedAt())
                    .changedAt(entity.getChangedAt())
                    .build();
        }

        Role role = Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .changedAt(entity.getChangedAt())
                .build();

        role.setRolePermissions(collectRolePermissions(entity, role, new HashSet<>()));
        return role;
    }

    private Set<RolePermission> collectRolePermissions(
            @NonNull RoleJpaEntity currentRole,
            @NonNull Role mappedRole,
            Set<String> visitedRoleKeys
    ) {
        String roleVisitKey = getRoleVisitKey(currentRole);
        if (!visitedRoleKeys.add(roleVisitKey)) {
            return new HashSet<>();
        }

        LinkedHashMap<String, RolePermission> aggregatedPermissions = new LinkedHashMap<>();

        if (currentRole.getRolePermissions() != null) {
            currentRole.getRolePermissions().forEach(rolePermissionJpa -> {
                RolePermission mappedPermission = toDomainRolePermission(rolePermissionJpa, mappedRole);
                String dedupeKey = getPermissionDedupeKey(mappedPermission);
                aggregatedPermissions.putIfAbsent(dedupeKey, mappedPermission);
            });
        }

        if (currentRole.getChildRoleRelations() != null) {
            currentRole.getChildRoleRelations().forEach(roleRelation -> {
                RoleJpaEntity childRole = roleRelation.getChildRole();
                collectRolePermissions(childRole, mappedRole, visitedRoleKeys)
                        .forEach(rolePermission -> aggregatedPermissions.putIfAbsent(
                                getPermissionDedupeKey(rolePermission),
                                rolePermission
                        ));
            });
        }

        return new HashSet<>(aggregatedPermissions.values());
    }

    private RolePermission toDomainRolePermission(@NonNull RolePermissionJpaEntity entity, @NonNull Role role) {
        return RolePermission.builder()
                .id(entity.getId())
                .permission(toDomain(entity.getPermission()))
                .role(role)
                .keyObjectAcl(entity.getKeyObjectAcl())
                .build();
    }

    private String getRoleVisitKey(@NonNull RoleJpaEntity role) {
        return role.getId() != null
                ? "id:" + role.getId()
                : "obj:" + System.identityHashCode(role);
    }

    private String getPermissionDedupeKey(@NonNull RolePermission rolePermission) {
        Permission permission = rolePermission.getPermission();
        if (permission.getId() != null) {
            return "id:" + permission.getId();
        }
        return "key:" + permission.getKey();
    }

    public RoleJpaEntity toEntity(@NonNull Role domain) {
        return RoleJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .createdAt(domain.getCreatedAt())
                .changedAt(domain.getChangedAt())
                .build();
    }

    // Permission mappings
    public Permission toDomain(@NonNull PermissionJpaEntity entity) {
        return Permission.builder()
                .id(entity.getId())
                .name(entity.getName())
                .key(entity.getKey())
                .keyObject(entity.getKeyObject())
                .build();
    }

    public PermissionJpaEntity toEntity(@NonNull Permission domain) {
        return PermissionJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .key(domain.getKey())
                .keyObject(domain.getKeyObject())
                .build();
    }

    // UserRole mappings
    private UserRole toDomainUserRole(@NonNull UserRoleJpaEntity entity, User user) {
        return UserRole.builder()
                .id(entity.getId())
                .user(user)
                .role(toDomain(entity.getRole()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // SignupUser mappings
    public SignupUser toDomain(@NonNull SignupUserJpaEntity entity) {
        return SignupUser.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .saltedPassword(entity.getSaltedPassword())
                .personalData(toDomain(entity.getPersonalData()))
                .isApproved(entity.getIsApproved())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public SignupUserJpaEntity toEntity(@NonNull SignupUser domain) {
        return SignupUserJpaEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .saltedPassword(domain.getSaltedPassword())
                .personalData(toEntity(domain.getPersonalData()))
                .isApproved(domain.getIsApproved())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
