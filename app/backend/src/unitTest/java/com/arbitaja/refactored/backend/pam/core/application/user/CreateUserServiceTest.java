package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.DuplicateEntityException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.Role;
import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import com.arbitaja.refactored.backend.pam.core.domain.model.SignupUser;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.in.user.CreateUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.personaldata.PersonalDataRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.role.RoleRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.school.SchoolRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.security.PasswordEncoderPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.SignupUserRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private SignupUserRepositoryPort signupUserRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    @Mock
    private SchoolRepositoryPort schoolRepository;

    @Mock
    private PersonalDataRepositoryPort personalDataRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private CreateUserService createUserService;

    @Test
    void signupUserCreatesSignupEntryWithEncodedPassword() {
        CreateUserUseCase.SignupCommand command = CreateUserUseCase.SignupCommand.builder()
            .username("alice")
            .password("raw")
            .fullName("Alice")
            .email("alice@example.com")
            .schoolId(3)
            .build();

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(signupUserRepository.existsByUsername("alice")).thenReturn(false);
        when(schoolRepository.findById(3)).thenReturn(Optional.of(School.builder().id(3).name("School").build()));
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(signupUserRepository.save(any(SignupUser.class))).thenAnswer(inv -> inv.getArgument(0));

        SignupUser result = createUserService.signupUser(command);

        assertEquals("alice", result.getUsername());
        assertEquals("encoded", result.getSaltedPassword());
        assertEquals("alice@example.com", result.getPersonalData().getEmail());
        verify(signupUserRepository).save(any(SignupUser.class));
    }

    @Test
    void signupUserThrowsWhenUsernameAlreadyTaken() {
        CreateUserUseCase.SignupCommand command = CreateUserUseCase.SignupCommand.builder()
            .username("taken")
            .password("p")
            .fullName("Taken")
            .email("taken@example.com")
            .build();

        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> createUserService.signupUser(command));
    }

    @Test
    void approveSignupUserCreatesRealUserAssignsDefaultRoleAndDeletesSignup() {
        SignupUser signupUser = SignupUser.builder()
            .id(10)
            .username("pending")
            .saltedPassword("hashed")
            .personalData(PersonalData.builder().fullName("Pending").email("pending@example.com").build())
            .build();

        Role userRole = Role.builder().id(2).name("user").build();
        PersonalData savedPersonal = PersonalData.builder().id(5).fullName("Approved").email("approved@example.com").build();
        User firstSave = User.builder().id(20).username("approved").saltedPassword("hashed").personalData(savedPersonal).build();
        User secondSave = User.builder().id(20).username("approved").saltedPassword("hashed").personalData(savedPersonal).build();

        CreateUserUseCase.ApproveSignupCommand command = CreateUserUseCase.ApproveSignupCommand.builder()
            .signupUserId(10)
            .username("approved")
            .fullName("Approved")
            .email("approved@example.com")
            .build();

        when(signupUserRepository.findById(10)).thenReturn(Optional.of(signupUser));
        when(personalDataRepository.save(any(PersonalData.class))).thenReturn(savedPersonal);
        when(userRepository.save(any(User.class))).thenReturn(firstSave, secondSave);
        when(roleRepository.findByName("user")).thenReturn(Optional.of(userRole));

        User result = createUserService.approveSignupUser(command);

        assertEquals(20, result.getId());
        assertEquals("approved", result.getUsername());
        verify(userRepository, times(2)).save(any(User.class));
        verify(signupUserRepository).delete(signupUser);
    }

    @Test
    void approveSignupUserThrowsWhenSignupMissing() {
        when(signupUserRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            createUserService.approveSignupUser(CreateUserUseCase.ApproveSignupCommand.builder().signupUserId(404).build())
        );
    }
}

