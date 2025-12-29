package io.github.luminaire1337.propertyvista.backend.entity;

import io.github.luminaire1337.propertyvista.backend.entity.utility.UserRole;
import io.github.luminaire1337.propertyvista.backend.entity.utility.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    // Core fields
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // User information fields
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "avatar_image_path")
    private String avatarImagePath;

    // Site specific fields
    @Column(nullable = false)
    private Integer propertyPoints;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Property> properties;

    // CreatedAt and UpdatedAt timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (role == null)
            role = UserRole.USER;
        if (status == null)
            status = UserStatus.UNVERIFIED;
        if (propertyPoints == null)
            propertyPoints = 7;

        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isSuspended() {
        return status == UserStatus.SUSPENDED;
    }

    public boolean isVerified() {
        return status == UserStatus.VERIFIED;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
