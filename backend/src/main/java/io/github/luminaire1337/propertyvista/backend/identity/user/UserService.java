package io.github.luminaire1337.propertyvista.backend.identity.user;

import io.github.luminaire1337.propertyvista.backend.identity.event.UserCreatedEvent;
import io.github.luminaire1337.propertyvista.backend.identity.event.UserDeletedEvent;
import io.github.luminaire1337.propertyvista.backend.identity.event.UserUpdatedAvatarEvent;
import io.github.luminaire1337.propertyvista.backend.identity.exception.UserAlreadyExistsException;
import io.github.luminaire1337.propertyvista.backend.identity.exception.UserNotFoundException;
import io.github.luminaire1337.propertyvista.backend.identity.verification.VerificationToken;
import io.github.luminaire1337.propertyvista.backend.identity.verification.VerificationTokenService;
import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.shared.ImagePath;
import io.github.luminaire1337.propertyvista.backend.shared.PhoneNumber;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Optional<User> findByEmail(EmailAddress email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(EmailAddress email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUserId(Long id) {
        return userRepository.findById(id);
    }

    public User getByUserId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean verifyPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Transactional
    public User createUser(EmailAddress email, String password, @Nullable UserRole role) {
        if (existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role != null ? role : UserRole.USER)
                .build();
        user = userRepository.save(user);

        VerificationToken token = verificationTokenService.generateToken(user);
        log.info("Created new user with ID {} and email {} and generated verification token {}", user.getId(), email, token.getToken());

        applicationEventPublisher.publishEvent(new UserCreatedEvent(user, token));
        return user;
    }

    @Transactional
    public User deleteUser(User user) {
        userRepository.delete(user);
        log.info("Deleted user with ID {}", user.getId());

        applicationEventPublisher.publishEvent(new UserDeletedEvent(user));
        return user;
    }

    @Transactional
    public User deleteUser(Long id) {
        User user = getByUserId(id);
        return deleteUser(user);
    }

    @Transactional
    public User updateEmail(Long id, EmailAddress email) {
        if (existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        User user = getByUserId(id);
        user.setEmail(email);
        user = userRepository.save(user);
        log.info("Updated email for user with ID {} to {}", id, email);
        return user;
    }

    @Transactional
    public User updatePassword(Long id, String password) {
        User user = getByUserId(id);
        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);
        log.info("Updated password for user with ID {}", id);
        return user;
    }

    @Transactional
    public User updateUserRole(Long id, UserRole role) {
        User user = getByUserId(id);
        user.setRole(role);
        user = userRepository.save(user);
        log.info("Updated role for user with ID {} to {}", id, role);
        return user;
    }

    @Transactional
    public User updateUserStatus(Long id, UserStatus status) {
        User user = getByUserId(id);
        user.setStatus(status);
        user = userRepository.save(user);
        log.info("Updated status for user with ID {} to {}", id, status);
        return user;
    }

    @Transactional
    public User updateUserInfo(Long id, String firstName, String lastName, PhoneNumber phoneNumber) {
        User user = getByUserId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user = userRepository.save(user);
        log.info("Updated information for user with ID {}", id);
        return user;
    }

    @Transactional
    public User updateAvatarImagePath(Long id, @Nullable ImagePath avatarImagePath) {
        User user = getByUserId(id);
        user.setAvatarImagePath(avatarImagePath);
        user = userRepository.save(user);
        log.info("Updated avatar image path for user with ID {}", id);

        if (user.getAvatarImagePath() != null) {
            applicationEventPublisher.publishEvent(new UserUpdatedAvatarEvent(user));
        }

        return user;
    }
}
