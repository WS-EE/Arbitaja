package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.Api_token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiTokenRepository extends JpaRepository<Api_token, Integer> {
    Optional<Api_token> findApi_tokenById(int id);
    Optional<Api_token> findByToken(String token);
}
