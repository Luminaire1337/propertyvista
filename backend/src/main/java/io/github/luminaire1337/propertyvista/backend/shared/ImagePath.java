package io.github.luminaire1337.propertyvista.backend.shared;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagePath {
    public static final String regexp = "^[a-zA-Z0-9/_-]+\\.(jpg|jpeg|png|gif|webp)$";
    public static final String message = "Invalid image path format";

    @Size(max = 255, message = "Image path must be at most 255 characters long")
    @Pattern(regexp = regexp, message = message)
    private String value;

    public static ImagePath valueOf(String value) {
        return new ImagePath(value);
    }

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
