package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.User;

public record UserDeletedEmail(
        User user
) implements EmailDetails {
    @Override
    public String getRecipientEmail() {
        return user.getEmail();
    }

    @Override
    public String getSubject() {
        return "Twoje konto zostało usunięte";
    }

    @Override
    public String getBody() {
        return """
                <p>Szanowny Użytkowniku,</p>
                <p>Z przykrością informujemy, że Twoje konto zostało usunięte z naszego systemu. Jeśli uważasz, że to pomyłka lub masz jakiekolwiek pytania, skontaktuj się z naszym zespołem wsparcia.</p>
                <p>Pozdrawiamy,<br/>Zespół Property Vista</p>
                <br/>
                <p>Prosimy nie odpowiadać na tę wiadomość, ponieważ jest to automatycznie generowany e-mail.</p>
                """;
    }
}
