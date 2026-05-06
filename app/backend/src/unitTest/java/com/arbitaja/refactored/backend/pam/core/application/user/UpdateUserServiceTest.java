package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.exception.ForbiddenException;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.School;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.in.user.UpdateUserUseCase;
import com.arbitaja.refactored.backend.pam.core.port.out.personaldata.PersonalDataRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.school.SchoolRepositoryPort;
import com.arbitaja.refactored.backend.pam.core.port.out.security.PasswordEncoderPort;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private SchoolRepositoryPort schoolRepository;

    @Mock
    private PersonalDataRepositoryPort personalDataRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private UpdateUserService updateUserService;

    @Test
    void updateUserProfileUpdatesFieldsAndPersists() {
        User user = User.builder()
            .id(6)
            .username("old")
            .saltedPassword("hash")
            .personalData(PersonalData.builder().fullName("Old Name").email("old@example.com").build())
            .build();

        UpdateUserUseCase.UpdateUserCommand command = UpdateUserUseCase.UpdateUserCommand.builder()
            .userId(6)
            .username("new")
            .fullName("New Name")
            .email("new@example.com")
            .schoolId(4)
            .build();

        when(userRepository.findById(6)).thenReturn(Optional.of(user));
        when(schoolRepository.findById(4)).thenReturn(Optional.of(School.builder().id(4).name("School").build()));
        when(personalDataRepository.save(any(PersonalData.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(user)).thenReturn(user);

        User result = updateUserService.updateUserProfile(command, "old", false);

        assertEquals("new", result.getUsername());
        assertEquals("New Name", result.getPersonalData().getFullName());
        assertEquals(4, result.getPersonalData().getSchool().getId());
        assertNotNull(result.getPersonalData().getCreatedAt());
    }

    @Test
    void updateUserProfileThrowsWhenNotAuthorized() {
        User user = User.builder().id(1).username("owner").saltedPassword("h").build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UpdateUserUseCase.UpdateUserCommand command = UpdateUserUseCase.UpdateUserCommand.builder()
            .userId(1)
            .username("owner")
            .fullName("Owner")
            .email("owner@example.com")
            .build();

        assertThrows(ForbiddenException.class, () -> updateUserService.updateUserProfile(command, "other", false));
    }

    @Test
    void changePasswordVerifiesOldPasswordAndSavesEncodedNewPassword() {
        User user = User.builder().id(2).username("alice").saltedPassword("old-hash").build();
        UpdateUserUseCase.ChangePasswordCommand command = UpdateUserUseCase.ChangePasswordCommand.builder()
            .userId(2)
            .oldPassword("old")
            .newPassword("new")
            .build();

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("new-hash");

        boolean changed = updateUserService.changePassword(command, "alice");

        assertTrue(changed);
        assertEquals("new-hash", user.getSaltedPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordThrowsWhenOldPasswordInvalid() {
        User user = User.builder().id(3).username("alice").saltedPassword("old-hash").build();
        UpdateUserUseCase.ChangePasswordCommand command = UpdateUserUseCase.ChangePasswordCommand.builder()
            .userId(3)
            .oldPassword("wrong")
            .newPassword("new")
            .build();

        when(userRepository.findById(3)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> updateUserService.changePassword(command, "alice"));
    }

    @Test
    void deleteUserThrowsWhenMissing() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> updateUserService.deleteUser(99));
    }
}

