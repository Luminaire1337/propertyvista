package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.entity.RefreshToken;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.exception.BadRequestException;
import io.github.luminaire1337.propertyvista.backend.exception.NotFoundException;
import io.github.luminaire1337.propertyvista.backend.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private RefreshToken getByRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));
    }

    public List<RefreshToken> findAllExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        return refreshTokenRepository.findAll().stream()
                .filter(token -> token.getExpiryDate().isBefore(now))
                .toList();
    }

    @Transactional
    public String generateRefreshToken(User user, String userAgent) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .userAgent(userAgent)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Generated refresh token for user {}: {}", user.getEmail(), refreshToken.getToken());
        return refreshToken.getToken();
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        RefreshToken token = getByRefreshToken(refreshToken);
        refreshTokenRepository.delete(token);
        log.info("Deleted refresh token {}", refreshToken);
    }

    @Transactional
    public User ensureTokenValid(String refreshToken) {
        RefreshToken token = getByRefreshToken(refreshToken);

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.info("Refresh token {} has expired", token.getToken());
            throw new BadRequestException("Refresh token has expired");
        }

        refreshTokenRepository.delete(token); // Delete token either way, so it can't be reused
        return token.getUser(); // Return the user to generate a new token
    }

    @Transactional
    public void deleteRefreshTokens(List<RefreshToken> tokens) {
        refreshTokenRepository.deleteAll(tokens);
        log.info("Deleted {} expired refresh tokens", tokens.size());
    }
}
