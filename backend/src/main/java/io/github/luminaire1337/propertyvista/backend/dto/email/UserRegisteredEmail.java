package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.User;

public record UserRegisteredEmail(
        User user,
        String verificationToken
) implements EmailDetails {
    @Override
    public String getRecipientEmail() {
        return String.valueOf(user.getEmail());
    }

    @Override
    public String getSubject() {
        return "Welcome to Property Vista! Please verify your account";
    }

    @Override
    public String getBody() {
        return """
                <p>Dear %s %s,</p>
                <p>Thank you for registering in Property Vista! To complete your registration, please verify your email address by clicking the link below:</p>
                <p><a href="{FRONTEND_URL}/verify-email?token=%s">Verify Email Address</a></p>
                """
                .formatted(
                        user.getFirstName(),
                        user.getLastName(),
                        verificationToken
                );
    }
}
