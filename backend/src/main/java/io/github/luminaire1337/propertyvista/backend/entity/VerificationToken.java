package io.github.luminaire1337.propertyvista.backend.entity;

import io.github.luminaire1337.propertyvista.backend.helper.TokenGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String token;

    @OneToOne()
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @PrePersist
    public void prePersist() {
        token = TokenGenerator.generateToken(32);
    }
}
