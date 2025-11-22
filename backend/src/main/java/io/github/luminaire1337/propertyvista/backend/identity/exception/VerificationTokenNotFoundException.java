package io.github.luminaire1337.propertyvista.backend.identity.exception;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException(String message) {
        super(message);
    }
}
