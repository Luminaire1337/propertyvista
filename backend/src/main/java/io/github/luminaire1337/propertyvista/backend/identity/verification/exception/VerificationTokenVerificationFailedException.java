package io.github.luminaire1337.propertyvista.backend.identity.verification.exception;

import io.github.luminaire1337.propertyvista.backend.shared.RestApiException;
import org.springframework.http.HttpStatus;

public class VerificationTokenVerificationFailedException extends RestApiException {
    public VerificationTokenVerificationFailedException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}