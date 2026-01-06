package io.github.luminaire1337.propertyvista.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class PropertyVistaBackendApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PropertyVistaBackendApiApplication.class, args);
    }
}
