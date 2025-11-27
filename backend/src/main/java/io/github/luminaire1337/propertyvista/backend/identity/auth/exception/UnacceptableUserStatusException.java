package io.github.luminaire1337.propertyvista.backend.identity.auth.exception;

import io.github.luminaire1337.propertyvista.backend.shared.RestApiException;
import org.springframework.http.HttpStatus;

public class UnacceptableUserStatusException extends RestApiException {
    public UnacceptableUserStatusException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
