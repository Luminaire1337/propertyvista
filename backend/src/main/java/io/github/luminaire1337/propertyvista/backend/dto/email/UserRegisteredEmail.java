package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.User;
import org.springframework.beans.factory.annotation.Value;

public record UserRegisteredEmail(
        User user,
        String verificationToken
) implements EmailDetails {
    @Value("${propertyvista.frontend.url}")
    private static String frontendUrl;

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
                <p><a href="%s/verify-email?token=%s">Verify Email Address</a></p>
                <p>If you did not register for Property Vista, please ignore this email.</p>
                <p>Best regards,<br/>The Property Vista Team</p>
                """
                .formatted(
                        user.getFirstName(),
                        user.getLastName(),
                        frontendUrl,
                        verificationToken
                );
    }
}
