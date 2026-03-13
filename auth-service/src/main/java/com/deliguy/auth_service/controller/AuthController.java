package com.deliguy.auth_service.controller;

import com.deliguy.auth_service.dto.AuthResponse;
import com.deliguy.auth_service.dto.LoginRequest;
import com.deliguy.auth_service.dto.RegisterRequest;
import com.deliguy.auth_service.model.RefreshToken;
import com.deliguy.auth_service.model.Role;
import com.deliguy.auth_service.model.User;
import com.deliguy.auth_service.repository.UserRepository;
import com.deliguy.auth_service.service.JwtService;
import com.deliguy.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;        
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Username or email already exists"));
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("check before Runtime Exception in Login method");

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(
            user.getUsername(),
            List.of(user.getRole().name())
        );

        RefreshToken refreshToken = refreshTokenService.create(user.getUsername());

        return new AuthResponse(accessToken, refreshToken.getToken(), 900, user.getRole().name());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String refreshToken) {
        RefreshToken oldToken = refreshTokenService.validate(refreshToken);
        RefreshToken newToken = refreshTokenService.rotate(oldToken);

       User user = userRepository.findByUsername(oldToken.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(
            oldToken.getUserId(),
            List.of(user.getRole().name()) 
        );

        return new AuthResponse(accessToken, newToken.getToken(), 900 , "CUSTOMER");
    }
}