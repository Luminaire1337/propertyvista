package io.github.luminaire1337.propertyvista.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class PropertyVistaBackendApiApplication {

    static void main(String[] args) {
        SpringApplication.run(PropertyVistaBackendApiApplication.class, args);
    }

}
