package io.github.luminaire1337.propertyvista.backend.vo;

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
public class PhoneNumber {
    // https://stackoverflow.com/a/23299989
    public static final String regexp = "^\\+[1-9]\\d{1,14}$";
    public static final String message = "Phone number must be in E.164 format (e.g., +1234567890)";

    @Size(max = 15, message = "Phone number must be at most 15 characters long")
    @Pattern(regexp = regexp, message = message)
    private String value;

    public static PhoneNumber valueOf(String value) {
        return new PhoneNumber(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
