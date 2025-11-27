package io.github.luminaire1337.propertyvista.backend.identity.auth;

import io.github.luminaire1337.propertyvista.backend.identity.auth.dto.AuthResponse;
import io.github.luminaire1337.propertyvista.backend.identity.user.User;
import io.github.luminaire1337.propertyvista.backend.identity.user.UserService;
import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId());
        UUID refreshToken = refreshTokenService.generateRefreshToken(user);
        Long expirationMs = jwtService.getExpirationMs();

        return new AuthResponse(accessToken, refreshToken, expirationMs);
    }

    @Transactional
    public AuthResponse register(EmailAddress email, String password) {
        User user = userService.createUser(email, password, null);
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(EmailAddress email, String password) {
        User user = userService.authenticateUser(email, password);
        return generateAuthResponse(user);
    }

    @Transactional
    public void logout(UUID refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    @Transactional
    public AuthResponse refreshAccessToken(UUID refreshTokenId) {
        RefreshToken refreshToken = refreshTokenService.ensureTokenValid(refreshTokenId);

        User user = refreshToken.getUser();
        return generateAuthResponse(user);
    }
}
