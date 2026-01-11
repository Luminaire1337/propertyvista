package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.User;

public record PaymentSucceededEmail(
        Integer propertyPoints,
        User user
) implements EmailDetails {
    @Override
    public String getRecipientEmail() {
        return user.getEmail();
    }

    @Override
    public String getSubject() {
        return "Potwierdzenie płatności za %d Property Points".formatted(propertyPoints);
    }

    @Override
    public String getBody() {
        return """
                <p>Szanowny Użytkowniku,</p>
                <p>Twoja płatność za <strong>%d</strong> Property Points została pomyślnie przetworzona.</p>
                <p>Dziękujemy za skorzystanie z naszych usług!</p>
                <p>Pozdrawiamy,<br/>Zespół Property Vista</p>
                <br/>
                <p>Prosimy nie odpowiadać na tę wiadomość, ponieważ jest to automatycznie generowany e-mail.</p>
                """.formatted(propertyPoints);
    }
}
