package com.scm.movieApp.service;

import com.scm.movieApp.entities.UserRole;
import com.scm.movieApp.entities.Users;
import com.scm.movieApp.exceptions.UsernameNotFoundException;
import com.scm.movieApp.repository.UserRepository;
import com.scm.movieApp.utils.AuthResponse;
import com.scm.movieApp.utils.LoginRequest;
import com.scm.movieApp.utils.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService
{
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, RefreshTokenService refreshTokenService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest)
    {
        var user = Users.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(encoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .build();

        Users savedUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(savedUser.getEmail());
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    @Transactional
    public AuthResponse verify(LoginRequest loginRequest)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        Users user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found "));
        var accessToken = jwtService.generateToken(user.getEmail());
        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
