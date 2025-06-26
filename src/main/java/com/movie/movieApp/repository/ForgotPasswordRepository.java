package com.movie.movieApp.repository;

import com.movie.movieApp.entities.ForgotPassword;
import com.movie.movieApp.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer>
{
    Optional<ForgotPassword> findByUser(Users user);

    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, Users user);
}
