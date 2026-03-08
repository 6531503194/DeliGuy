package com.deliguy.auth_service.controller;

import com.deliguy.auth_service.dto.AuthResponse;
import com.deliguy.auth_service.dto.LoginRequest;
import com.deliguy.auth_service.model.RefreshToken;
import com.deliguy.auth_service.model.Role;
import com.deliguy.auth_service.model.User;
import com.deliguy.auth_service.repository.UserRepository;
import com.deliguy.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Registering user: " + user.getUsername() + " with role: " + user.getRole()  + " and password: " + user.getEmail());
        userRepository.save(user);
        return "User registered";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {



        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("auth-service")
            .issuedAt(now)
            .expiresAt(now.plus(15, ChronoUnit.MINUTES)) // ⏱ short-lived
            .subject(user.getUsername())
            .claim("roles", List.of(user.getRole().name()))
            .build();

        String accessToken = jwtEncoder.encode(
            JwtEncoderParameters.from(claims)
        ).getTokenValue();

        RefreshToken refreshToken =
            refreshTokenService.create(user.getUsername());

        return new AuthResponse(
            accessToken,
            refreshToken.getToken(),
            900 // seconds (15 min)
        );
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String refreshToken) {

        RefreshToken oldToken =
            refreshTokenService.validate(refreshToken);

        RefreshToken newToken =
            refreshTokenService.rotate(oldToken);

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("auth-service")
            .issuedAt(now)
            .expiresAt(now.plus(15, ChronoUnit.MINUTES))
            .subject(oldToken.getUserId())
            .claim("roles", List.of("CUSTOMER")) // later fetch from DB
            .build();

        String accessToken = jwtEncoder.encode(
            JwtEncoderParameters.from(claims)
        ).getTokenValue();

        return new AuthResponse(
            accessToken,
            newToken.getToken(),
            900
        );
    }
}
