package io.github.luminaire1337.propertyvista.backend.shared;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagePath {
    @Pattern(regexp = "^[a-zA-Z0-9/_-]+\\.(jpg|jpeg|png|gif|webp)$", message = "Invalid image path format")
    private String value;

    public String getFileExtension() {
        if (value == null || !value.contains(".")) {
            return null;
        }
        return value.substring(value.lastIndexOf('.') + 1).toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
