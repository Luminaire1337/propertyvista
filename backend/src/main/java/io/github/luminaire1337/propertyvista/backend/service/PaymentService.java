package io.github.luminaire1337.propertyvista.backend.service;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.luminaire1337.propertyvista.backend.entity.Payment;
import io.github.luminaire1337.propertyvista.backend.entity.User;
import io.github.luminaire1337.propertyvista.backend.entity.utility.PaymentStatus;
import io.github.luminaire1337.propertyvista.backend.exception.BadRequestException;
import io.github.luminaire1337.propertyvista.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private static final Integer PLN_PER_PROPERTY_POINT = 5;
    private final PaymentRepository paymentRepository;

    public Integer getCurrentRate() {
        return PLN_PER_PROPERTY_POINT;
    }

    public String createPaymentIntent(Integer propertyPoints, User user) {
        double amountInPLN = (double) propertyPoints * PLN_PER_PROPERTY_POINT;

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
            var msg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            payment.setFailureReason(msg.substring(0, Math.min(msg.length(), 1000)));
            paymentRepository.save(payment);
            log.error("Error while creating payment intent for payment {}: {}", payment.getId(), e.getMessage());
            throw new BadRequestException("Nie udało się utworzyć płatności");
        }
    }
}
