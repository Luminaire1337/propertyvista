package io.github.luminaire1337.propertyvista.backend.repository;

import io.github.luminaire1337.propertyvista.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
}
