package com.movie.movieApp.repository;

import com.movie.movieApp.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>
{

    RefreshToken findByRefreshToken(String refreshToken);
}
