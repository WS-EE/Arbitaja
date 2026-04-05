package com.arbitaja.refactored.backend.pam.core.application.user;

import com.arbitaja.refactored.backend.pam.core.domain.exception.EntityNotFoundException;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import com.arbitaja.refactored.backend.pam.core.port.out.user.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private GetUserService getUserService;

    @Test
    void getUserByUsernameDelegatesToRepository() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(User.builder().id(1).username("alice").saltedPassword("h").build()));

        Optional<User> result = getUserService.getUserByUsername("alice");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void getAllUsersDelegatesToRepository() {
        when(userRepository.findAll()).thenReturn(List.of(User.builder().id(2).username("bob").saltedPassword("h").build()));

        List<User> result = getUserService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("bob", result.getFirst().getUsername());
    }

    @Test
    void getUserProfileThrowsWhenUserMissing() {
        when(userRepository.findById(77)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> getUserService.getUserProfile(77));
    }
}

