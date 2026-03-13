package com.deliguy.auth_service.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    String role
) {}
