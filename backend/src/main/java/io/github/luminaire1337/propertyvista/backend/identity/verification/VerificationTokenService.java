package io.github.luminaire1337.propertyvista.backend.identity.verification;

import io.github.luminaire1337.propertyvista.backend.identity.user.User;
import io.github.luminaire1337.propertyvista.backend.identity.user.UserService;
import io.github.luminaire1337.propertyvista.backend.identity.verification.exception.VerificationTokenNotFoundException;
import io.github.luminaire1337.propertyvista.backend.identity.verification.exception.VerificationTokenVerificationFailedException;
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
    private final UserService userService;

    private LocalDateTime getExpiryDate() {
        return LocalDateTime.now().plusHours(24);
    }

    public VerificationToken getByToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new VerificationTokenNotFoundException("Verification token " + token + " not found"));
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
                .token(String.valueOf(UUID.randomUUID()))
                .user(user)
                .expiryDate(getExpiryDate())
                .build();

        verificationTokenRepository.save(token);
        log.info("Generated verification token for user {}: {}", user.getEmail(), token.getToken());
        return token;
    }

    @Transactional
    public VerificationToken generateToken(Long userId) {
        User user = userService.getByUserId(userId);
        return generateToken(user);
    }

    @Transactional
    public VerificationToken verifyToken(User user, String token) {
        VerificationToken verificationToken = getByToken(token);

        if (!verificationToken.getUser().getId().equals(user.getId())) {
            log.info("Verification token {} does not belong to user {}", token, user.getEmail());
            throw new VerificationTokenVerificationFailedException("Provided token does not belong to the specified user");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.info("Verification token {} for user {} has expired", token, user.getEmail());
            throw new VerificationTokenVerificationFailedException("Verification token has expired");
        }

        verificationTokenRepository.delete(verificationToken);
        log.info("Verification token {} for user {} has been verified and deleted", token, user.getEmail());
        return verificationToken;
    }

    @Transactional
    public VerificationToken verifyToken(Long userId, String token) {
        User user = userService.getByUserId(userId);
        return verifyToken(user, token);
    }
}
