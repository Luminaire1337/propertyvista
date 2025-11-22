package io.github.luminaire1337.propertyvista.backend.identity.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
