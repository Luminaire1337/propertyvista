package io.github.luminaire1337.propertyvista.backend.entity;

import io.github.luminaire1337.propertyvista.backend.entity.listener.PropertyImageListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "property_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(PropertyImageListener.class)
public class PropertyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "image_path", unique = true, nullable = false)
    private String imagePath;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    // CreatedAt and UpdatedAt timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
