package com.deliguy.auth_service.dto;

public record LoginRequest(
    String email,
    String password
) {}
