package io.github.luminaire1337.propertyvista.backend.service;

import io.github.luminaire1337.propertyvista.backend.dto.email.UserDeletedEmail;
import io.github.luminaire1337.propertyvista.backend.dto.email.UserRegisteredEmail;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.utility.UserRole;
import io.github.luminaire1337.propertyvista.backend.entity.utility.UserStatus;
import io.github.luminaire1337.propertyvista.backend.exception.BadRequestException;
import io.github.luminaire1337.propertyvista.backend.exception.ForbiddenAccessException;
import io.github.luminaire1337.propertyvista.backend.exception.NotFoundException;
import io.github.luminaire1337.propertyvista.backend.helper.BucketNames;
import io.github.luminaire1337.propertyvista.backend.repository.UserRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final StorageService storageService;
    private final ImageService imageService;

    public User getByUserId(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Użytkownik o podanym ID nie istnieje"));
    }

    @Transactional
    public User createUser(String email, String password, String firstName, String lastName, String phoneNumber, @Nullable UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Użytkownik z podanym adresem e-mail już istnieje");
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

        emailService.sendEmailAsync(new UserRegisteredEmail(user, token));
        return user;
    }

    @Transactional
    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Konto o podanym adresie e-mail nie istnieje"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Nieprawidłowe hasło");
        }

        if (!user.isEnabled()) {
            throw new ForbiddenAccessException("Twoje konto nie zostało zweryfikowane. Sprawdź swoją skrzynkę e-mail, aby zweryfikować konto.");
        }

        if (!user.isAccountNonLocked()) {
            throw new ForbiddenAccessException("Twoje konto zostało zablokowane. Skontaktuj się z administratorem.");
        }

        return user;
    }

    @Transactional
    public User deleteUser(User user) {
        deleteUserOldAvatar(user);
        userRepository.delete(user);
        log.info("Deleted user with ID {}", user.getId());

        emailService.sendEmailAsync(new UserDeletedEmail(user));
        return user;
    }

    @Transactional
    public User updateUserEmail(User user, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Użytkownik z podanym adresem e-mail już istnieje");
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
            storageService.deleteFileIfExists(BucketNames.PUBLIC_AVATAR_IMAGES, currentAvatarPath);
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
            throw new BadRequestException("Nieprawidłowy format lub rozmiar pliku obrazu awatara");
        }

        // Generate unique filename for the new avatar image
        String newFileName = imageService.generateImageFileName(avatarImage);

        // Upload new avatar image
        var uploadResponse = storageService.uploadFile(BucketNames.PRIVATE_AVATAR_IMAGES, newFileName, avatarImage);
        if (uploadResponse == null) {
            throw new BadRequestException("Nie udało się przesłać obrazu awatara. Spróbuj ponownie później.");
        }

        // Delete old avatar image if exists
        deleteUserOldAvatar(user);
        user.setAvatarImagePath(null);
        user = userRepository.save(user);

        // Process image asynchronously in the background
        UUID userId = user.getId();
        imageService.validateImagesContentAsync(List.of(uploadResponse), (success) -> {
            User finalUser = userRepository.findById(userId).orElse(null);

            if (finalUser != null && success) {
                // Move image from private to public bucket after processing
                storageService.moveFileBetweenBuckets(BucketNames.PRIVATE_AVATAR_IMAGES, BucketNames.PUBLIC_AVATAR_IMAGES, newFileName);
                finalUser.setAvatarImagePath(newFileName);
                userRepository.save(finalUser);
                log.info("User's avatar image is now available in public bucket for user ID {}", userId);
            } else {
                log.error("Failed to process avatar image for user ID {}", userId);
                storageService.deleteFileIfExists(BucketNames.PRIVATE_AVATAR_IMAGES, newFileName);
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
