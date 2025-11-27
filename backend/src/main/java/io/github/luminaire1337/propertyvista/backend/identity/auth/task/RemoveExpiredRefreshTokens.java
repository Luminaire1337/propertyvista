package io.github.luminaire1337.propertyvista.backend.identity.auth.task;

import io.github.luminaire1337.propertyvista.backend.identity.auth.entity.RefreshToken;
import io.github.luminaire1337.propertyvista.backend.identity.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemoveExpiredRefreshTokens {
    private final RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 0 */1 * *")
    public void removeExpiredRefreshTokens() {
        log.info("Running 'removeExpiredRefreshTokens' task");

        List<RefreshToken> expiredTokens = refreshTokenService.findAllExpiredTokens();
        refreshTokenService.deleteRefreshTokens(expiredTokens);
    }
}
