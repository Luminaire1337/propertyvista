package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.Property;
import io.github.luminaire1337.propertyvista.backend.entity.User;

public record PropertyExpiredEmail(
        Property property,
        User user
) implements EmailDetails {
    @Override
    public String getRecipientEmail() {
        return user.getEmail();
    }

    @Override
    public String getSubject() {
        return "Twoje ogłoszenie '%s' wygasło".formatted(property.getTitle());
    }

    @Override
    public String getBody() {
        return """
                <p>Szanowny Użytkowniku,</p>
                <p>Informujemy, że Twoje ogłoszenie '<strong>%s</strong>' wygasło i nie jest już widoczne na naszej platformie.</p>
                <p>Jeśli chcesz odnowić swoje ogłoszenie, zaloguj się na swoje konto i przedłuż jego ważność.</p>
                <p>Pozdrawiamy,<br/>Zespół Property Vista</p>
                <br/>
                <p>Prosimy nie odpowiadać na tę wiadomość, ponieważ jest to automatycznie generowany e-mail.</p>
                """.formatted(property.getTitle());
    }

}
