package io.github.luminaire1337.propertyvista.backend.entity;

import io.github.luminaire1337.propertyvista.backend.vo.EmailAddress;
import io.github.luminaire1337.propertyvista.backend.vo.ImagePath;
import io.github.luminaire1337.propertyvista.backend.vo.PhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    // Core fields
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @NotNull(message = "Email is required")
    @Valid
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    })
    private EmailAddress email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // User information fields
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    @Column(nullable = false)
    private String lastName;

    @NotNull(message = "Phone number is required")
    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "phone_number", nullable = false))
    })
    private PhoneNumber phoneNumber;

    @Embedded
    @Valid
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "avatar_image_path"))
    })
    private ImagePath avatarImagePath;

    // Site specific fields
    @Column(nullable = false)
    private Integer propertyPoints;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return String.valueOf(email);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.VERIFIED;
    }
}
