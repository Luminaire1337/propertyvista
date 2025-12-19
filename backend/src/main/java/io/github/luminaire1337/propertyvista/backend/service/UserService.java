package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.dto.email.UserDeletedEmail;
import io.github.luminaire1337.propertyvista.backend.dto.email.UserRegisteredEmail;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.UserRole;
import io.github.luminaire1337.propertyvista.backend.entity.UserStatus;
import io.github.luminaire1337.propertyvista.backend.exception.*;
import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.github.luminaire1337.propertyvista.backend.repository.UserRepository;
import io.minio.ObjectWriteResponse;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MinioService minioService;
    private final ImageService imageService;

    public User getByUserId(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    @Transactional
    public User createUser(String email, String password, String firstName, String lastName, String phoneNumber, @Nullable UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
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
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserPasswordVerificationFailedException("User password verification failed");
        }

        if (!user.isEnabled()) {
            throw new ForbiddenAccessException("User is not verified");
        }

        if (!user.isAccountNonLocked()) {
            throw new ForbiddenAccessException("User account is suspended");
        }

        return user;
    }

    @Transactional
    public User deleteUser(User user) {
        deleteUserOldAvatar(user);
        userRepository.delete(user);
        log.info("Deleted user with ID {}", user.getId());

        emailService.sendEmail(new UserDeletedEmail(user));
        return user;
    }

    @Transactional
    public User updateUserEmail(User user, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
        user.setEmail(email);
        user = userRepository.save(user);
        log.info("Updated email for user with ID {} to {}", user.getId(), email);
        return user;
    }

    @Transactional
    public User updateUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
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
    public User updateUserInfo(User user, String firstName, String lastName, String phoneNumber) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user = userRepository.save(user);
        log.info("Updated information for user with ID {}", user.getId());
        return user;
    }

    private void deleteUserOldAvatar(User user) {
        String currentAvatarPath = user.getAvatarImagePath();
        if (currentAvatarPath != null && !currentAvatarPath.isBlank()) {
            minioService.deleteObjectIfExists(BucketNames.PUBLIC_AVATAR_IMAGES, currentAvatarPath);
        }
    }

    @Transactional
    public User updateUserAvatarImage(User user, @Nullable MultipartFile avatarImage) {
        if (avatarImage == null || avatarImage.isEmpty()) {
            deleteUserOldAvatar(user);
            user.setAvatarImagePath(null);
            user = userRepository.save(user);
            log.info("Removed avatar image for user with ID {}", user.getId());
            return user;
        }

        // Validate new avatar image
        if (!imageService.isImageValid(avatarImage)) {
            throw new InvalidImageException("Uploaded avatar image is invalid");
        }

        // Generate unique filename for the new avatar image
        String newFileName = UUID.randomUUID() + "." + imageService.getImageExtension(avatarImage);

        // Upload new avatar image
        ObjectWriteResponse uploadResponse = minioService.uploadFile(BucketNames.PRIVATE_AVATAR_IMAGES, newFileName, avatarImage);
        if (uploadResponse == null) {
            throw new InvalidImageException("Failed to upload avatar image");
        }

        // Delete old avatar image if exists
        deleteUserOldAvatar(user);
        user.setAvatarImagePath(null);
        user = userRepository.save(user);

        // Process image asynchronously in the background
        UUID userId = user.getId();
        imageService.processImage(avatarImage, (success) -> {
            User finalUser = userRepository.findById(userId).orElse(null);

            if (finalUser != null && success) {
                // Move image from private to public bucket after processing
                minioService.moveObjectBetweenBuckets(BucketNames.PRIVATE_AVATAR_IMAGES, BucketNames.PUBLIC_AVATAR_IMAGES, newFileName);
                finalUser.setAvatarImagePath(newFileName);
                userRepository.save(finalUser);
                log.info("User's avatar image is now available in public bucket for user ID {}", userId);
            } else {
                log.error("Failed to process avatar image for user ID {}", userId);
                minioService.deleteObjectIfExists(BucketNames.PRIVATE_AVATAR_IMAGES, newFileName);
            }
        });
        return user;
    }

    @Transactional
    public void verifyUser(String token) {
        User user = verificationTokenService.verifyToken(token);
        updateUserStatus(user, UserStatus.VERIFIED);
    }
}
