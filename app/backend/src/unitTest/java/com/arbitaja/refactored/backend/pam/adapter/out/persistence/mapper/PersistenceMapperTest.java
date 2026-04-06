package com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PermissionJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PersonalDataJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RoleJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RolePermissionJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.RoleRelationJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.SchoolJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.SignupUserJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.UserJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.UserRoleJpaEntity;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceMapperTest {

    private final PersistenceMapper mapper = new PersistenceMapper();

    @Test
    void toDomainUserMapsNestedDataAndRoles() {
        RoleJpaEntity roleEntity = RoleJpaEntity.builder().id(9).name("user").build();
        UserJpaEntity userEntity = UserJpaEntity.builder()
            .id(3)
            .username("alice")
            .saltedPassword("hash")
            .personalData(PersonalDataJpaEntity.builder()
                .id(4)
                .fullName("Alice")
                .email("alice@example.com")
                .school(SchoolJpaEntity.builder().id(2).name("School").build())
                .build())
            .build();
        userEntity.getUserRoles().add(UserRoleJpaEntity.builder().id(1).user(userEntity).role(roleEntity).build());

        User domain = mapper.toDomain(userEntity);

        assertEquals(3, domain.getId());
        assertEquals("alice", domain.getUsername());
        assertEquals("Alice", domain.getPersonalData().getFullName());
        assertEquals(1, domain.getUserRoles().size());
        assertEquals("user", domain.getUserRoles().iterator().next().getRole().getName());
    }

    @Test
    void toDomainRoleCollectsChildRolePermissionsWithoutInfiniteRecursion() {
        PermissionJpaEntity viewUsers = PermissionJpaEntity.builder().id(1).name("View users").key("VIEW_USERS").build();
        PermissionJpaEntity viewRoles = PermissionJpaEntity.builder().id(2).name("View roles").key("VIEW_ROLES").build();

        RoleJpaEntity parent = RoleJpaEntity.builder().id(10).name("parent").build();
        RoleJpaEntity child = RoleJpaEntity.builder().id(11).name("child").build();

        parent.getRolePermissions().add(RolePermissionJpaEntity.builder().id(100).role(parent).permission(viewUsers).build());
        child.getRolePermissions().add(RolePermissionJpaEntity.builder().id(101).role(child).permission(viewRoles).build());
        child.getRolePermissions().add(RolePermissionJpaEntity.builder().id(102).role(child).permission(viewUsers).build());

        parent.getChildRoleRelations().add(RoleRelationJpaEntity.builder().id(201).parentRole(parent).childRole(child).build());
        child.getChildRoleRelations().add(RoleRelationJpaEntity.builder().id(202).parentRole(child).childRole(parent).build());

        Role mapped = mapper.toDomain(parent);

        assertEquals("parent", mapped.getName());
        assertEquals(2, mapped.getRolePermissions().size());
        assertTrue(mapped.getRolePermissions().stream().anyMatch(rp -> "VIEW_USERS".equals(rp.getPermission().getKey())));
        assertTrue(mapped.getRolePermissions().stream().anyMatch(rp -> "VIEW_ROLES".equals(rp.getPermission().getKey())));
    }

    @Test
    void toEntityAndBackForSignupUserKeepsImportantFields() {
        SignupUser domain = SignupUser.builder()
            .id(22)
            .username("pending")
            .saltedPassword("hash")
            .isApproved(false)
            .createdAt(Instant.now())
            .personalData(com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData.builder()
                .id(5)
                .fullName("Pending User")
                .email("pending@example.com")
                .school(com.arbitaja.refactored.backend.pam.core.domain.model.School.builder().id(1).name("School").build())
                .build())
            .build();

        SignupUserJpaEntity entity = mapper.toEntity(domain);
        SignupUser remapped = mapper.toDomain(entity);

        assertNotNull(entity.getPersonalData());
        assertEquals(22, remapped.getId());
        assertEquals("pending", remapped.getUsername());
        assertEquals("pending@example.com", remapped.getPersonalData().getEmail());
    }
}

