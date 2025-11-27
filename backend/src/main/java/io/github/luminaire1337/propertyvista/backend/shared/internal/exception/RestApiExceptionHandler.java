package io.github.luminaire1337.propertyvista.backend.shared.internal.exception;

import io.github.luminaire1337.propertyvista.backend.shared.ErrorResponse;
import io.github.luminaire1337.propertyvista.backend.shared.RestApiException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RestApiExceptionHandler {
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ErrorResponse> handleRestApiException(RestApiException ex) {
        return ResponseEntity
                .status(ex.getStatus() != null ? ex.getStatus() : HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }
}
