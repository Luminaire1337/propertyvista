package io.github.luminaire1337.propertyvista.backend.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class VerificationTokenNotFoundExceptionHandler {
    @ExceptionHandler(VerificationTokenNotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(VerificationTokenNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
    }
}
