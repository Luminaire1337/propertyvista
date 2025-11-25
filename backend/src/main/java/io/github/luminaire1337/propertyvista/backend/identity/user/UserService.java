package io.github.luminaire1337.propertyvista.backend.identity.user;

import io.github.luminaire1337.propertyvista.backend.identity.user.dto.UserResponse;
import io.github.luminaire1337.propertyvista.backend.identity.user.event.UserCreatedEvent;
import io.github.luminaire1337.propertyvista.backend.identity.user.event.UserDeletedEvent;
import io.github.luminaire1337.propertyvista.backend.identity.user.event.UserUpdatedAvatarEvent;
import io.github.luminaire1337.propertyvista.backend.identity.user.exception.UserAlreadyExistsException;
import io.github.luminaire1337.propertyvista.backend.identity.user.exception.UserNotFoundException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserMapper userMapper;

    private User getByUserId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    @Transactional
    public UserResponse createUser(EmailAddress email, String password, @Nullable UserRole role) {
        if (userRepository.existsByEmail(email)) {
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
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponse deleteUser(User user) {
        userRepository.delete(user);
        log.info("Deleted user with ID {}", user.getId());

        applicationEventPublisher.publishEvent(new UserDeletedEvent(user));
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponse deleteUser(Long id) {
        User user = getByUserId(id);
        return deleteUser(user);
    }

    @Transactional
    public UserResponse updateUserEmail(User user, EmailAddress email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
        user.setEmail(email);
        user = userRepository.save(user);
        log.info("Updated email for user with ID {} to {}", user.getId(), email);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponse updateUserEmail(Long id, EmailAddress email) {
        User user = getByUserId(id);
        return updateUserEmail(user, email);
    }

    @Transactional
    public UserResponse updateUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);
        log.info("Updated password for user with ID {}", user.getId());
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponse updateUserPassword(Long id, String password) {
        User user = getByUserId(id);
        return updateUserPassword(user, password);
    }

    @Transactional
    public UserResponse updateUserRole(User user, UserRole role) {
        user.setRole(role);
        user = userRepository.save(user);
        log.info("Updated role for user with ID {} to {}", user.getId(), role);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponse updateUserRole(Long id, UserRole role) {
        User user = getByUserId(id);
        return updateUserRole(user, role);
    }

    @Transactional
    public UserResponse updateUserStatus(User user, UserStatus status) {
        user.setStatus(status);
        user = userRepository.save(user);
        log.info("Updated status for user with ID {} to {}", user.getId(), status);
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponse updateUserStatus(Long id, UserStatus status) {
        User user = getByUserId(id);
        return updateUserStatus(user, status);
    }

    @Transactional
    public UserResponse updateUserInfo(User user, String firstName, String lastName, PhoneNumber phoneNumber) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user = userRepository.save(user);
        log.info("Updated information for user with ID {}", user.getId());
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponse updateUserInfo(Long id, String firstName, String lastName, PhoneNumber phoneNumber) {
        User user = getByUserId(id);
        return updateUserInfo(user, firstName, lastName, phoneNumber);
    }

    @Transactional
    public UserResponse updateUserAvatarImagePath(User user, @Nullable ImagePath avatarImagePath) {
        user.setAvatarImagePath(avatarImagePath);
        user = userRepository.save(user);
        log.info("Updated avatar image path for user with ID {}", user.getId());

        if (user.getAvatarImagePath() != null) {
            applicationEventPublisher.publishEvent(new UserUpdatedAvatarEvent(user));
        }

        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponse updateUserAvatarImagePath(Long id, @Nullable ImagePath avatarImagePath) {
        User user = getByUserId(id);
        return updateUserAvatarImagePath(user, avatarImagePath);
    }
}
