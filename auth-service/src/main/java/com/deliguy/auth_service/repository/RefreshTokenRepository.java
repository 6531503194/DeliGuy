package com.deliguy.auth_service.repository;

import com.deliguy.auth_service.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, String> {
}
