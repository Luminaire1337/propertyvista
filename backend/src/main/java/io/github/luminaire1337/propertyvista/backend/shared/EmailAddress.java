package io.github.luminaire1337.propertyvista.backend.shared;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAddress {
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String value;

    @Override
    public String toString() {
        return value;
    }
}
