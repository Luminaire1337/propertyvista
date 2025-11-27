package io.github.luminaire1337.propertyvista.backend.identity.auth.service;

import io.github.luminaire1337.propertyvista.backend.identity.auth.dto.AuthResponse;
import io.github.luminaire1337.propertyvista.backend.identity.auth.entity.RefreshToken;
import io.github.luminaire1337.propertyvista.backend.identity.user.dto.UserResponse;
import io.github.luminaire1337.propertyvista.backend.identity.user.entity.User;
import io.github.luminaire1337.propertyvista.backend.identity.user.mapper.UserMapper;
import io.github.luminaire1337.propertyvista.backend.identity.user.service.UserService;
import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.shared.PhoneNumber;
import io.github.luminaire1337.propertyvista.backend.shared.SafePassword;
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
    private final UserMapper userMapper;

    @Transactional
    protected AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId());
        UUID refreshToken = refreshTokenService.generateRefreshToken(user);
        Long expirationMs = jwtService.getExpirationMs();

        return new AuthResponse(accessToken, refreshToken, expirationMs);
    }

    @Transactional
    public UserResponse register(EmailAddress email, SafePassword password, String firstName, String lastName, PhoneNumber phoneNumber) {
        User user = userService.createUser(email, password, firstName, lastName, phoneNumber, null);
        return userMapper.toDTO(user);
    }

    @Transactional
    public AuthResponse login(EmailAddress email, SafePassword password) {
        User user = userService.authenticateUser(email, password);
        return generateAuthResponse(user);
    }

    @Transactional
    public void logout(UUID refreshTokenId) {
        refreshTokenService.deleteRefreshToken(refreshTokenId);
    }

    @Transactional
    public AuthResponse refreshAccessToken(UUID refreshTokenId) {
        RefreshToken refreshToken = refreshTokenService.ensureTokenValid(refreshTokenId);

        User user = refreshToken.getUser();
        return generateAuthResponse(user);
    }
}
