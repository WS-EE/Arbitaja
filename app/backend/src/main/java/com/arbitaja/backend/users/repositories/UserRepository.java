package com.arbitaja.backend.users.repositories;

import com.arbitaja.backend.users.dataobjects.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    User findUserByUsername(String username);

    @Modifying
    @Query(value = """
    INSERT INTO "user" (id, username, salted_password, personal_data_id)
        VALUES (:#{#user.id}, :#{#user.username}, :#{#user.salted_password}, :#{#user.personal_data.id})
        ON CONFLICT (id)
        DO UPDATE SET
            username = EXCLUDED.username,
            salted_password = EXCLUDED.salted_password,
            personal_data_id = EXCLUDED.personal_data_id
    """, nativeQuery = true)
    void updateUser(User user);
}
