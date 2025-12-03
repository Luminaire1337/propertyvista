package io.github.luminaire1337.propertyvista.backend.dto.email;

public interface EmailDetails {
    String getRecipientEmail();

    String getSubject();

    String getBody();
}
