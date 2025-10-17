package io.github.luminaire1337.propertyvista.backend.shared.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Property Vista - Backend API",
                version = "0.0.1-SNAPSHOT",
                description = "Backend API for Property Vista project",
                contact = @Contact(
                        name = "GitHub Repository",
                        url = "https://github.com/Luminaire1337/propertyvista")
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local server"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)

@SecurityScheme(
        name = "bearerAuth",
        description = "Your JWT token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
