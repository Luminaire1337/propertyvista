package io.github.luminaire1337.propertyvista.backend.controller;

import io.github.luminaire1337.propertyvista.backend.dto.request.CreatePaymentIntentRequest;
import io.github.luminaire1337.propertyvista.backend.dto.response.ErrorResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.PaymentIntentResponse;
import io.github.luminaire1337.propertyvista.backend.dto.response.PaymentRateResponse;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.security.CurrentUserContext;
import io.github.luminaire1337.propertyvista.backend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments management", description = "Endpoints for payment processing and management")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid request payload", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
        }),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
        }),
        @ApiResponse(responseCode = "403", description = "Forbidden access", content = {
                @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class))
        })
})
public class PaymentController {
    private final PaymentService paymentService;
    private final CurrentUserContext currentUserContext;

    @GetMapping("/rate")
    @Operation(
            summary = "Get current property point rate",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of current rate", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentRateResponse.class))
                    })
            }
    )
    public ResponseEntity<PaymentRateResponse> getCurrentRate() {
        Integer currentRate = paymentService.getCurrentRate();
        return ResponseEntity.status(HttpStatus.OK).body(new PaymentRateResponse(currentRate));
    }

    @PostMapping("/create-intent")
    @Operation(
            summary = "Create a payment intent for purchasing property points",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment intent created successfully", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentIntentResponse.class))
                    })
            }
    )
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(@Valid @RequestBody CreatePaymentIntentRequest request) {
        User user = currentUserContext.getEntity();
        String paymentIntentId = paymentService.createPaymentIntent(request.propertyPoints(), user);
        return ResponseEntity.status(HttpStatus.OK).body(new PaymentIntentResponse(paymentIntentId));
    }
}
