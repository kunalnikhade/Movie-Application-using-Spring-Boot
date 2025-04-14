package com.scm.movieApp.controllers;

import com.scm.movieApp.entities.RefreshToken;
import com.scm.movieApp.entities.Users;
import com.scm.movieApp.service.JwtService;
import com.scm.movieApp.service.RefreshTokenService;
import com.scm.movieApp.service.UserService;
import com.scm.movieApp.utils.AuthResponse;
import com.scm.movieApp.utils.LoginRequest;
import com.scm.movieApp.utils.RefreshTokenRequest;
import com.scm.movieApp.utils.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/")
public class UserController
{
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> getRegister(@RequestBody RegisterRequest registerRequest)
    {
        return ResponseEntity.ok(userService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> getLogin(@RequestBody LoginRequest loginRequest)
    {
        return ResponseEntity.ok(userService.verify(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> getRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest)
    {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        Users user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }
}
