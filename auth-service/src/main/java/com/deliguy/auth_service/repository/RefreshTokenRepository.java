package com.deliguy.auth_service.repository;

import com.deliguy.auth_service.model.RefreshToken;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByExpiryDateBefore(Instant now);
    void deleteByRevokedTrue();
}
