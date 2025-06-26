package com.movie.movieApp.repository;

import com.movie.movieApp.entities.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer>
{
    Optional<Users> findByEmail(String username);

    @Transactional
    @Modifying
    @Query("update Users u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String newPassword);
}
