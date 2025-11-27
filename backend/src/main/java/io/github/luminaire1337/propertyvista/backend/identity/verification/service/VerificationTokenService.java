package io.github.luminaire1337.propertyvista.backend.identity.verification.service;

import io.github.luminaire1337.propertyvista.backend.identity.user.entity.User;
import io.github.luminaire1337.propertyvista.backend.identity.verification.entity.VerificationToken;
import io.github.luminaire1337.propertyvista.backend.identity.verification.exception.VerificationTokenNotFoundException;
import io.github.luminaire1337.propertyvista.backend.identity.verification.exception.VerificationTokenVerificationFailedException;
import io.github.luminaire1337.propertyvista.backend.identity.verification.repository.VerificationTokenRepository;
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
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    private LocalDateTime getExpiryDate() {
        return LocalDateTime.now().plusHours(24);
    }

    private VerificationToken getByVerificationTokenId(UUID id) {
        return verificationTokenRepository.findById(id)
                .orElseThrow(() -> new VerificationTokenNotFoundException("Verification token not found"));
    }

    public List<VerificationToken> findAllExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        return verificationTokenRepository.findAll().stream()
                .filter(token -> token.getExpiryDate().isBefore(now))
                .toList();
    }

    @Transactional
    public VerificationToken generateToken(User user) {
        VerificationToken token = VerificationToken.builder()
                .user(user)
                .expiryDate(getExpiryDate())
                .build();

        token = verificationTokenRepository.save(token);
        log.info("Generated verification token for user {}: {}", user.getEmail(), token.getId());
        return token;
    }

    @Transactional
    public VerificationToken verifyToken(UUID id, User user) {
        VerificationToken token = getByVerificationTokenId(id);

        if (!token.getUser().getId().equals(user.getId())) {
            log.info("Verification token {} does not belong to user {}", id, user.getEmail());
            throw new VerificationTokenVerificationFailedException("Provided token does not belong to the specified user");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.info("Verification token {} for user {} has expired", id, user.getEmail());
            throw new VerificationTokenVerificationFailedException("Verification token has expired");
        }

        verificationTokenRepository.delete(token);
        log.info("Verification token {} for user {} has been verified and deleted", id, user.getEmail());
        return token;
    }
}
