package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    User findUserByUsername(String username);
}
