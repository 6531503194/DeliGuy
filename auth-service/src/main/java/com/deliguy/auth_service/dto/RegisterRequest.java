package com.deliguy.auth_service.dto;

import com.deliguy.auth_service.model.Role;

public record RegisterRequest(
    String username,
    String email,
    String password,
    Role role
) {}
