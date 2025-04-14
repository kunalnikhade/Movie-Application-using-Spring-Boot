package com.scm.movieApp.service;

import com.scm.movieApp.entities.RefreshToken;
import com.scm.movieApp.entities.Users;
import com.scm.movieApp.exceptions.RefreshTokenExpired;
import com.scm.movieApp.exceptions.UsernameNotFoundException;
import com.scm.movieApp.repository.RefreshTokenRepository;
import com.scm.movieApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
