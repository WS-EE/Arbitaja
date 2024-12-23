package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.SignupUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignupUserRepository extends JpaRepository<SignupUser, Integer> {
    Optional<SignupUser> findByUsername(String username);
}
