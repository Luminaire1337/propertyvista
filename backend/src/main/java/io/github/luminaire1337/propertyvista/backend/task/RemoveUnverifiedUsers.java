package io.github.luminaire1337.propertyvista.backend.task;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.VerificationToken;
import io.github.luminaire1337.propertyvista.backend.entity.utility.UserStatus;
import io.github.luminaire1337.propertyvista.backend.service.UserService;
import io.github.luminaire1337.propertyvista.backend.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemoveUnverifiedUsers {
    private final VerificationTokenService verificationTokenService;
    private final UserService userService;

    @Scheduled(cron = "0 0 */6 * * *")
    public void removeUnverifiedUsers() {
        log.info("Running 'removeUnverifiedUsers' task");

        List<VerificationToken> expiredTokens = verificationTokenService.findAllExpiredTokens();
        for (VerificationToken token : expiredTokens) {
            User user = token.getUser();
            if (user.getStatus() == UserStatus.UNVERIFIED) {
                userService.deleteUser(user);
            } else {
                log.warn("User {} has an expired verification token but was not unverified", user.getEmail());
            }
        }
    }
}
