package com.deliguy.auth_service.service;

import com.deliguy.auth_service.model.RefreshToken;
import com.deliguy.auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshToken create(String userId) {

        RefreshToken token = new RefreshToken(
            UUID.randomUUID().toString(),
            userId,
            Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return repository.save(token);
    }

    public RefreshToken validate(String tokenValue) {

        RefreshToken token = repository.findById(tokenValue)
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.isRevoked())
            throw new RuntimeException("Token revoked");

        if (token.getExpiryDate().isBefore(Instant.now()))
            throw new RuntimeException("Token expired");

        return token;
    }

    public RefreshToken rotate(RefreshToken oldToken) {
        oldToken.setRevoked(true);
        repository.save(oldToken);

        return create(oldToken.getUserId());
    }
}
