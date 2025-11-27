package io.github.luminaire1337.propertyvista.backend.identity.auth;

import io.github.luminaire1337.propertyvista.backend.identity.auth.exception.InvalidRefreshTokenException;
import io.github.luminaire1337.propertyvista.backend.identity.user.User;
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

    public UUID generateRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Generated refresh token for user {}: {}", user.getEmail(), refreshToken.getId());
        return refreshToken.getId();
    }

    public void deleteRefreshToken(UUID refreshToken) {
        refreshTokenRepository.findById(refreshToken).ifPresent(token -> {
            refreshTokenRepository.delete(token);
            log.info("Deleted refresh token {}", token.getId());
        });
    }

    public RefreshToken ensureTokenValid(UUID refreshTokenId) {
        RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenId)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

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

    public void deleteRefreshTokens(List<RefreshToken> tokens) {
        refreshTokenRepository.deleteAll(tokens);
        log.info("Deleted {} expired refresh tokens", tokens.size());
    }
}
