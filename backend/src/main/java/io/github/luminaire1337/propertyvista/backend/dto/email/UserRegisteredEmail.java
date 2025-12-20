package io.github.luminaire1337.propertyvista.backend.dto.email;

import io.github.luminaire1337.propertyvista.backend.entity.User;

public record UserRegisteredEmail(
        User user,
        String verificationToken
) implements EmailDetails {
    @Override
    public String getRecipientEmail() {
        return user.getEmail();
    }

    @Override
    public String getSubject() {
        return "Potwierdź swój adres e-mail";
    }

    @Override
    public String getBody() {
        return """
                <p>Szanowny/a %s %s,</p>
                <p>Dziękujemy za rejestrację w naszym serwisie. Aby zakończyć proces rejestracji, prosimy o potwierdzenie swojego adresu e-mail, klikając w poniższy link:</p>
                <p><a href="{FRONTEND_URL}/verify-email?token=%s">Potwierdź adres e-mail</a></p>
                <p>Jeśli nie dokonywałeś/aś rejestracji, zignoruj tę wiadomość.</p>
                <p>Pozdrawiamy,<br/>Zespół Property Vista</p>
                <br/>
                <p>Prosimy nie odpowiadać na tę wiadomość, ponieważ jest to automatycznie generowany e-mail.</p>
                """
                .formatted(
                        user.getFirstName(),
                        user.getLastName(),
                        verificationToken
                );
    }
}
