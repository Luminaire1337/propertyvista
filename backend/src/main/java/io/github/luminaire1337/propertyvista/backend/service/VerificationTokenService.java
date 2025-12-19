package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.VerificationToken;
import io.github.luminaire1337.propertyvista.backend.exception.BadRequestException;
import io.github.luminaire1337.propertyvista.backend.exception.NotFoundException;
import io.github.luminaire1337.propertyvista.backend.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    private LocalDateTime getExpiryDate() {
        return LocalDateTime.now().plusHours(24);
    }

    private VerificationToken getByVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Token weryfikacyjny nie znaleziony"));
    }

    public List<VerificationToken> findAllExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        return verificationTokenRepository.findAll().stream()
                .filter(token -> token.getExpiryDate().isBefore(now))
                .toList();
    }

    @Transactional
    public String generateToken(User user) {
        VerificationToken token = VerificationToken.builder()
                .user(user)
                .expiryDate(getExpiryDate())
                .build();

        token = verificationTokenRepository.save(token);
        log.info("Generated verification token for user {}: {}", user.getEmail(), token.getToken());
        return token.getToken();
    }

    @Transactional
    public User verifyToken(String token) {
        VerificationToken verificationToken = getByVerificationToken(token);
        User user = verificationToken.getUser();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.info("Verification token {} for user {} has expired", token, user.getEmail());
            throw new BadRequestException("Token weryfikacyjny wygasł");
        }

        verificationTokenRepository.delete(verificationToken);
        log.info("Verification token {} for user {} has been verified and deleted", token, user.getEmail());
        return user;
    }
}
