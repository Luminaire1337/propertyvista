package io.github.luminaire1337.propertyvista.backend.helper;

import java.security.SecureRandom;
import java.util.Base64;

public abstract class TokenGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateToken(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
