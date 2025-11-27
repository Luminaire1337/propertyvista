package io.github.luminaire1337.propertyvista.backend.identity.auth.exception;

import io.github.luminaire1337.propertyvista.backend.shared.RestApiException;
import org.springframework.http.HttpStatus;

public class InvalidAccessTokenException extends RestApiException {
    public InvalidAccessTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
