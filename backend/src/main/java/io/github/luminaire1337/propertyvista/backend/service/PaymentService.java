package io.github.luminaire1337.propertyvista.backend.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.luminaire1337.propertyvista.backend.dto.email.PaymentSucceededEmail;
import io.github.luminaire1337.propertyvista.backend.entity.Payment;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.utility.PaymentStatus;
import io.github.luminaire1337.propertyvista.backend.exception.BadRequestException;
import io.github.luminaire1337.propertyvista.backend.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private static final double PLN_PER_PROPERTY_POINT = 5.00;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final EmailService emailService;

    @Value("${PROPERTYVISTA_STRIPE_WEBHOOK_KEY}")
    private String stripeWebhookKey;

    public Double getCurrentRate() {
        return PLN_PER_PROPERTY_POINT;
    }

    @Transactional
    public String createPaymentIntent(Integer propertyPoints, User user) {
        double amountInPLN = propertyPoints * PLN_PER_PROPERTY_POINT;

        // Let's create a payment record in our database
        Payment payment = Payment.builder()
                .user(user)
                .amount(amountInPLN)
                .build();

        payment = paymentRepository.save(payment);

        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("payment_id", payment.getId().toString());
            metadata.put("user_id", user.getId().toString());
            metadata.put("property_points", propertyPoints.toString());

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(Math.round(amountInPLN * 100)) // Stripe expects amount in integer
                    .setCurrency("pln")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putAllMetadata(metadata)
                    .setDescription("Zakup %d Property Points".formatted(propertyPoints))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            // Update payment with stripe's payment intent ID
            payment.setStripePaymentIntentId(intent.getId());
            paymentRepository.save(payment);

            return intent.getClientSecret();
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);

            String message = e.getMessage();
            if (message != null) {
                payment.setFailureReason(message.substring(0, Math.min(message.length(), 1000)));
            }

            paymentRepository.save(payment);
            log.error("Error while creating payment intent for payment {}: {}", payment.getId(), e.getMessage());
            throw new BadRequestException("Nie udało się utworzyć płatności");
        }
    }

    @Transactional
    public void handleWebhookStatus(Event event, PaymentStatus status) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow(() ->
                new BadRequestException("Nie można przetworzyć obiektu zdarzenia webhooka Stripe")
        );
        Payment payment = paymentRepository.findByStripePaymentIntentId(intent.getId())
                .orElseThrow(() -> new BadRequestException("Nie znaleziono płatności dla podanego identyfikatora Payment Intent"));

        payment.setStatus(status);

        switch (status) {
            case SUCCEEDED -> {
                User user = payment.getUser();
                if (user == null) {
                    throw new BadRequestException("Nie można znaleźć użytkownika powiązanego z tą płatnością (został usunięty?)");
                }

                String propertyPointsStr = intent.getMetadata().get("property_points");
                if (propertyPointsStr == null) {
                    throw new BadRequestException("Brak informacji o punktach nieruchomości w metadanych płatności");
                }

                int propertyPoints = Integer.parseInt(propertyPointsStr);
                if (propertyPoints <= 0) {
                    throw new BadRequestException("Nieprawidłowa liczba punktów nieruchomości w metadanych płatności");
                }

                userService.giveUserPropertyPoints(user, propertyPoints);
                emailService.sendEmailAsync(new PaymentSucceededEmail(propertyPoints, user));
                log.info("Payment intent {} succeeded", intent.getId());
            }
            case FAILED -> {
                String message = intent.getLastPaymentError() != null ?
                        intent.getLastPaymentError().getMessage() : null;
                if (message != null) {
                    payment.setFailureReason(message.substring(0, Math.min(message.length(), 1000)));
                }
                log.info("Payment intent {} failed", intent.getId());
            }
            case CANCELED -> {
                String message = intent.getCancellationReason();
                if (message != null) {
                    payment.setFailureReason(message.substring(0, Math.min(message.length(), 1000)));
                }
                log.info("Payment intent {} got canceled", intent.getId());
            }
            default -> log.warn("Payment intent {} got unknown status: {}", intent.getId(), status);
        }

        paymentRepository.save(payment);
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, stripeWebhookKey
            );
        } catch (SignatureVerificationException e) {
            throw new BadRequestException("Nieprawidłowy podpis webhooka Stripe");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> handleWebhookStatus(event, PaymentStatus.SUCCEEDED);
            case "payment_intent.payment_failed" -> handleWebhookStatus(event, PaymentStatus.FAILED);
            case "payment_intent.canceled" -> handleWebhookStatus(event, PaymentStatus.CANCELED);
            default ->
                    throw new BadRequestException("Nieobsługiwany typ zdarzenia webhooka Stripe: " + event.getType());
        }
    }
}
