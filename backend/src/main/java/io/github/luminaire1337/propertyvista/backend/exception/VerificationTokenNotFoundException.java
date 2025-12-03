package io.github.luminaire1337.propertyvista.backend.exception;

import org.springframework.http.HttpStatus;

public class VerificationTokenNotFoundException extends RestApiException {
    public VerificationTokenNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}