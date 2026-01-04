package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.Property;
import io.github.luminaire1337.propertyvista.backend.entity.User;

public record PropertyApprovedEmail(
        Property property,
        User user
) implements EmailDetails {

    @Override
    public String getRecipientEmail() {
        return user.getEmail();
    }

    @Override
    public String getSubject() {
        return "Twoja nieruchomość '%s' jest teraz dostępna publicznie".formatted(property.getTitle());
    }

    @Override
    public String getBody() {
        return """
                <p>Szanowny Użytkowniku,</p>
                <p>Z przyjemnością informujemy, że Twoja nieruchomość '<strong>%s</strong>' została zweryfikowana i jest teraz dostępna publicznie na naszej platformie.</p>
                <p><a href="{FRONTEND_URL}/property/%s">Przejdź do oferty</a></p>
                <p>Pozdrawiamy,<br/>Zespół Property Vista</p>
                <br/>
                <p>Prosimy nie odpowiadać na tę wiadomość, ponieważ jest to automatycznie generowany e-mail.</p>
                """.formatted(
                property.getTitle(),
                property.getSlug()
        );
    }
}
