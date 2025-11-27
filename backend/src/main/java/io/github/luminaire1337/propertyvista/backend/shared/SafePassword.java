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
public class SafePassword {
    // https://uibakery.io/regex-library/password
    public static final String regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
    public static final String message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character";

    @Size(max = 64, message = "Password must be at most 64 characters long")
    @Pattern(regexp = regexp, message = message)
    private String value;

    public static SafePassword valueOf(String value) {
        return new SafePassword(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
