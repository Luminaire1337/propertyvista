package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class UnacceptableUserStatusException extends RestApiException {
    public UnacceptableUserStatusException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
