package io.github.luminaire1337.propertyvista.backend.shared;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAddress {
    public static final String message = "Invalid email format";

    @Size(max = 100, message = "Email must be at most 100 characters long")
    @Email(message = message)
    private String value;

    public static EmailAddress valueOf(String value) {
        return new EmailAddress(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
