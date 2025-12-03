package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.User;

public record UserDeletedEmail(
        User user
) implements EmailDetails {
    @Override
    public String getRecipientEmail() {
        return String.valueOf(user.getEmail());
    }

    @Override
    public String getSubject() {
        return "Your account has been deleted";
    }

    @Override
    public String getBody() {
        return """
                <p>Dear %s %s,</p>
                <p>We regret to inform you that your account has been deleted from our system. If you believe this was a mistake or have any questions, please contact our support team.</p>
                """
                .formatted(user.getFirstName(), user.getLastName());
    }
}
