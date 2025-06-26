package com.movie.movieApp.service;

import com.movie.movieApp.entities.RefreshToken;
import com.movie.movieApp.entities.Users;
import com.movie.movieApp.exceptions.RefreshTokenExpired;
import com.movie.movieApp.exceptions.UsernameNotFoundException;
import com.movie.movieApp.repository.RefreshTokenRepository;
import com.movie.movieApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService
{
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository)
    {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username)
    {
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email : " + username));

        RefreshToken refreshToken = user.getRefreshToken();

        if(refreshToken == null)
        {
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expiresTime(Instant.now().plusMillis(50 * 60 * 60 * 10000))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }

        return refreshToken;
    }

    @Transactional
    public RefreshToken verifyRefreshToken(String refreshToken)
    {
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken);

        if(refToken.getExpiresTime().compareTo(Instant.now()) < 0)
        {
            refreshTokenRepository.delete(refToken);
            throw new RefreshTokenExpired("Refresh Token Expired");
        }

        return refToken;
    }
}
