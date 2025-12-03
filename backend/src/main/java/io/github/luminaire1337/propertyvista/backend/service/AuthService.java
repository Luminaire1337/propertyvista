package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.dto.response.AuthResponse;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.mapper.UserMapper;
import io.github.luminaire1337.propertyvista.backend.vo.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.vo.SafePassword;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    @Transactional
    protected AuthResponse generateAuthResponse(User user, String userAgent) {
        String accessToken = jwtService.generateAccessToken(user.getId());
        String refreshToken = refreshTokenService.generateRefreshToken(user, userAgent);
        Long expirationMs = jwtService.getExpirationMs();

        return new AuthResponse(accessToken, refreshToken, expirationMs);
    }

    @Transactional
    public AuthResponse login(EmailAddress email, SafePassword password, String userAgent) {
        User user = userService.authenticateUser(email, password);
        return generateAuthResponse(user, userAgent);
    }

    @Transactional
    public void logout(String token) {
        refreshTokenService.deleteRefreshToken(token);
    }

    @Transactional
    public AuthResponse refreshAccessToken(String token, String userAgent) {
        User user = refreshTokenService.ensureTokenValid(token);
        return generateAuthResponse(user, userAgent);
    }
}
