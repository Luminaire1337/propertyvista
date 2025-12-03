package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.dto.email.UserDeletedEmail;
import io.github.luminaire1337.propertyvista.backend.dto.email.UserRegisteredEmail;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.UserRole;
import io.github.luminaire1337.propertyvista.backend.entity.UserStatus;
import io.github.luminaire1337.propertyvista.backend.exception.UnacceptableUserStatusException;
import io.github.luminaire1337.propertyvista.backend.exception.UserAlreadyExistsException;
import io.github.luminaire1337.propertyvista.backend.exception.UserNotFoundException;
import io.github.luminaire1337.propertyvista.backend.exception.UserPasswordVerificationFailedException;
import io.github.luminaire1337.propertyvista.backend.repository.UserRepository;
import io.github.luminaire1337.propertyvista.backend.vo.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.vo.ImagePath;
import io.github.luminaire1337.propertyvista.backend.vo.PhoneNumber;
import io.github.luminaire1337.propertyvista.backend.vo.SafePassword;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User getByUserId(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    @Transactional
    public User createUser(EmailAddress email, SafePassword password, String firstName, String lastName, PhoneNumber phoneNumber, @Nullable UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(String.valueOf(password)))
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();
        user = userRepository.save(user);

        String token = verificationTokenService.generateToken(user);
        log.info("Created new user with ID {} and email {} and generated verification token {}", user.getId(), email, token);

        emailService.sendEmail(new UserRegisteredEmail(user, token));
        return user;
    }

    @Transactional
    public User authenticateUser(EmailAddress email, SafePassword password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if (!passwordEncoder.matches(String.valueOf(password), user.getPassword())) {
            throw new UserPasswordVerificationFailedException("User password verification failed");
        }

        if (!user.isEnabled()) {
            throw new UnacceptableUserStatusException("User is not verified");
        }

        if (!user.isAccountNonLocked()) {
            throw new UnacceptableUserStatusException("User account is suspended");
        }

        return user;
    }

    @Transactional
    public User deleteUser(User user) {
        userRepository.delete(user);
        log.info("Deleted user with ID {}", user.getId());

        emailService.sendEmail(new UserDeletedEmail(user));
        return user;
    }

    @Transactional
    public User updateUserEmail(User user, EmailAddress email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
        user.setEmail(email);
        user = userRepository.save(user);
        log.info("Updated email for user with ID {} to {}", user.getId(), email);
        return user;
    }

    @Transactional
    public User updateUserEmail(UUID id, EmailAddress email) {
        User user = getByUserId(id);
        return updateUserEmail(user, email);
    }

    @Transactional
    public User updateUserPassword(User user, SafePassword password) {
        user.setPassword(passwordEncoder.encode(String.valueOf(password)));
        user = userRepository.save(user);
        log.info("Updated password for user with ID {}", user.getId());
        return user;
    }

    @Transactional
    public User updateUserRole(User user, UserRole role) {
        user.setRole(role);
        user = userRepository.save(user);
        log.info("Updated role for user with ID {} to {}", user.getId(), role);
        return user;
    }

    @Transactional
    public User updateUserStatus(User user, UserStatus status) {
        user.setStatus(status);
        user = userRepository.save(user);
        log.info("Updated status for user with ID {} to {}", user.getId(), status);
        return user;
    }

    @Transactional
    public User updateUserInfo(User user, String firstName, String lastName, PhoneNumber phoneNumber) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user = userRepository.save(user);
        log.info("Updated information for user with ID {}", user.getId());
        return user;
    }

    @Transactional
    public User updateUserAvatarImagePath(User user, @Nullable ImagePath avatarImagePath) {
        user.setAvatarImagePath(avatarImagePath);
        user = userRepository.save(user);
        log.info("Updated avatar image path for user with ID {}", user.getId());
        return user;
    }

    @Transactional
    public User verifyUser(String token) {
        User user = verificationTokenService.verifyToken(token);
        user = updateUserStatus(user, UserStatus.VERIFIED);
        return user;
    }
}
