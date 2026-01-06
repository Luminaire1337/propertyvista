package io.github.luminaire1337.propertyvista.backend.entity;

import com.github.slugify.Slugify;
import io.github.luminaire1337.propertyvista.backend.entity.utility.PropertyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {
    // Core fields
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Property status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status;

    // Property details
    @Column(length = 5000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Double area;

    @Column(nullable = false)
    private Integer rooms;

    @Column(nullable = false)
    private Boolean parking;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    // Images
    @OneToMany(mappedBy = "property", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PropertyImage> images;

    // CreatedAt and UpdatedAt timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (slug == null || slug.isBlank()) {
            var slugifier = Slugify.builder()
                    .lowerCase(true)
                    .build();
            slug = slugifier.slugify(System.currentTimeMillis() + " " + title);
        }
        if (status == null)
            status = PropertyStatus.UNVERIFIED;

        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        if (slug == null || slug.isBlank()) {
            var slugifier = Slugify.builder()
                    .lowerCase(true)
                    .build();
            slug = slugifier.slugify(System.currentTimeMillis() + " " + title);
        }

        updatedAt = LocalDateTime.now();
    }

    public boolean isPublished() {
        return status == PropertyStatus.PUBLISHED;
    }

    public PropertyImage getPrimaryImage() {
        return images != null ? images.stream()
                .filter(PropertyImage::isPrimary)
                .findFirst()
                .orElse(null) : null;
    }
}
