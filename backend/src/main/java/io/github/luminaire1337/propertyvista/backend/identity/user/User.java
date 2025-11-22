package io.github.luminaire1337.propertyvista.backend.identity.user;

import io.github.luminaire1337.propertyvista.backend.shared.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.shared.ImagePath;
import io.github.luminaire1337.propertyvista.backend.shared.PhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    // Core fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    })
    private EmailAddress email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.UNVERIFIED;

    // User information fields
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "phone_number", nullable = false))
    })
    private PhoneNumber phoneNumber;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "avatar_image_path"))
    })
    private ImagePath avatarImagePath;

    // Site specific fields
    @Column(nullable = false)
    private Integer propertyPoints;

    // CreatedAt and UpdatedAt timestamps
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
