package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.Property;
import io.github.luminaire1337.propertyvista.backend.entity.User;

public record PropertyRejectedEmail(
        Property property,
        User user
) implements EmailDetails {
    @Override
    public String getRecipientEmail() {
        return user.getEmail();
    }

    @Override
    public String getSubject() {
        return "Twoja nieruchomość '%s' została odrzucona".formatted(property.getTitle());
    }

    @Override
    public String getBody() {
        return """
                <p>Szanowny Użytkowniku,</p>
                <p>Niestety, Twoja nieruchomość '<strong>%s</strong>' nie spełniła naszych kryteriów weryfikacji i została odrzucona.</p>
                <p>Zachęcamy do zapoznania się z naszymi wytycznymi dotyczącymi dodawania nieruchomości i ponownego przesłania oferty po wprowadzeniu niezbędnych poprawek.</p>
                <p>Pozdrawiamy,<br/>Zespół Property Vista</p>
                <br/>
                <p>Prosimy nie odpowiadać na tę wiadomość, ponieważ jest to automatycznie generowany e-mail.</p>
                """.formatted(property.getTitle());
    }
}
