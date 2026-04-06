package com.arbitaja.refactored.backend.pam.adapter.out.persistence;

import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.PersonalDataJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.entity.UserJpaEntity;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.mapper.PersistenceMapper;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.PersonalDataJpaRepository;
import com.arbitaja.refactored.backend.pam.adapter.out.persistence.repository.UserJpaRepository;
import com.arbitaja.refactored.backend.pam.core.domain.model.PersonalData;
import com.arbitaja.refactored.backend.pam.core.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PersonalDataJpaRepository personalDataJpaRepository;

    @Mock
    private PersistenceMapper mapper;

    @InjectMocks
    private UserPersistenceAdapter adapter;

    @Test
    void saveReattachesExistingPersonalDataReference() {
        User user = User.builder()
            .username("alice")
            .saltedPassword("hash")
            .personalData(PersonalData.builder().id(8).fullName("Alice").email("alice@example.com").build())
            .build();
        UserJpaEntity entity = UserJpaEntity.builder().username("alice").saltedPassword("hash").build();
        PersonalDataJpaEntity managedRef = PersonalDataJpaEntity.builder().id(8).fullName("Alice").email("alice@example.com").build();
        UserJpaEntity savedEntity = UserJpaEntity.builder().id(20).username("alice").saltedPassword("hash").build();
        User savedDomain = User.builder().id(20).username("alice").saltedPassword("hash").build();

        when(mapper.toEntity(user)).thenReturn(entity);
        when(personalDataJpaRepository.getReferenceById(8)).thenReturn(managedRef);
        when(userJpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        User result = adapter.save(user);

        assertSame(managedRef, entity.getPersonalData());
        assertEquals(20, result.getId());
        verify(personalDataJpaRepository).getReferenceById(8);
    }

    @Test
    void saveSkipsReattachWhenPersonalDataIdMissing() {
        User user = User.builder()
            .username("alice")
            .saltedPassword("hash")
            .personalData(PersonalData.builder().fullName("Alice").email("alice@example.com").build())
            .build();
        UserJpaEntity entity = UserJpaEntity.builder().username("alice").saltedPassword("hash").build();
        UserJpaEntity savedEntity = UserJpaEntity.builder().id(21).username("alice").saltedPassword("hash").build();
        User savedDomain = User.builder().id(21).username("alice").saltedPassword("hash").build();

        when(mapper.toEntity(user)).thenReturn(entity);
        when(userJpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        User result = adapter.save(user);

        assertEquals(21, result.getId());
        verify(personalDataJpaRepository, never()).getReferenceById(anyInt());
    }

    @Test
    void deleteDelegatesToDeleteById() {
        User user = User.builder().id(6).username("alice").saltedPassword("hash").build();

        adapter.delete(user);

        verify(userJpaRepository).deleteById(6);
    }

    @Test
    void existsByUsernameDelegatesToRepository() {
        when(userJpaRepository.existsByUsername("alice")).thenReturn(true);

        boolean exists = adapter.existsByUsername("alice");

      assertTrue(exists);
    }
}

