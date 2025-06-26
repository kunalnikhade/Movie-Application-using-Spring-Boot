package com.movie.movieApp.controllers;

import com.movie.movieApp.entities.RefreshToken;
import com.movie.movieApp.entities.Users;
import com.movie.movieApp.service.JwtService;
import com.movie.movieApp.service.RefreshTokenService;
import com.movie.movieApp.service.UserService;
import com.movie.movieApp.utils.AuthResponse;
import com.movie.movieApp.utils.LoginRequest;
import com.movie.movieApp.utils.RefreshTokenRequest;
import com.movie.movieApp.utils.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> getRegister(@RequestBody RegisterRequest registerRequest)
    {
        return ResponseEntity.ok(userService.register(registerRequest));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> getLogin(@RequestBody LoginRequest loginRequest)
    {
        return ResponseEntity.ok(userService.verify(loginRequest));
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
