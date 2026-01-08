package io.github.luminaire1337.propertyvista.backend.runners;

import io.github.luminaire1337.propertyvista.backend.entity.utility.UserRole;
import io.github.luminaire1337.propertyvista.backend.repository.UserRepository;
import io.github.luminaire1337.propertyvista.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
public class FirstUserInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${PROPERTYVISTA_FIRST_USER_EMAIL}")
    private String firstUserEmail;

    @Value("${PROPERTYVISTA_FIRST_USER_PASSWORD}")
    private String firstUserPassword;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Check if there are any users in the system
        if (userRepository.count() > 0) {
            return;
        }

        // Create the first user
        userService.createUser(
                firstUserEmail,
                firstUserPassword,
                "First",
                "User",
                "+48123456789",
                UserRole.ADMIN
        );

        log.info("Admin user created with credentials: {} / {}", firstUserEmail, firstUserPassword);
        log.info("Visit provided email to verify the account.");
    }
}
