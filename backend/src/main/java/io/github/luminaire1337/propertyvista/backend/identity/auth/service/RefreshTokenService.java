package io.github.luminaire1337.propertyvista.backend.identity.auth.service;

import io.github.luminaire1337.propertyvista.backend.identity.auth.entity.RefreshToken;
import io.github.luminaire1337.propertyvista.backend.identity.auth.exception.InvalidRefreshTokenException;
import io.github.luminaire1337.propertyvista.backend.identity.auth.repository.RefreshTokenRepository;
import io.github.luminaire1337.propertyvista.backend.identity.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private RefreshToken getByRefreshTokenId(UUID id) {
        return refreshTokenRepository.findById(id)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token " + id + " not found"));
    }

    @Transactional
    public UUID generateRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Generated refresh token for user {}: {}", user.getEmail(), refreshToken.getId());
        return refreshToken.getId();
    }

    @Transactional
    public void deleteRefreshToken(UUID refreshToken) {
        RefreshToken token = getByRefreshTokenId(refreshToken);
        refreshTokenRepository.delete(token);
        log.info("Deleted refresh token {}", refreshToken);
    }

    @Transactional
    public RefreshToken ensureTokenValid(UUID refreshTokenId) {
        RefreshToken refreshToken = getByRefreshTokenId(refreshTokenId);

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.info("Refresh token {} has expired", refreshTokenId);
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }
        return refreshToken;
    }

    public List<RefreshToken> findAllExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        return refreshTokenRepository.findAll().stream()
                .filter(token -> token.getExpiryDate().isBefore(now))
                .toList();
    }

    @Transactional
    public void deleteRefreshTokens(List<RefreshToken> tokens) {
        refreshTokenRepository.deleteAll(tokens);
        log.info("Deleted {} expired refresh tokens", tokens.size());
    }
}
