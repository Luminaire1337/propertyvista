package io.github.luminaire1337.propertyvista.backend.config;

import io.github.luminaire1337.propertyvista.backend.entity.utility.UserRole;
import io.github.luminaire1337.propertyvista.backend.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Swagger endpoints
                        .requestMatchers("/actuator/**").permitAll() // Actuator endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll() // User registration
                        .requestMatchers(HttpMethod.POST, "/users/verify-email").permitAll() // Email verification
                        .requestMatchers(HttpMethod.GET, "/properties").permitAll() // Public property listings
                        .requestMatchers(HttpMethod.GET, "/properties/{slug}").permitAll() // Public property details
                        .requestMatchers("/admin/**").hasRole(UserRole.ADMIN.toString())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Cors only gets applied in dev profile
        if (corsConfigurationSource != null) {
            http.cors(cors -> cors.configurationSource(corsConfigurationSource));
        }

        return http.build();
    }
}
