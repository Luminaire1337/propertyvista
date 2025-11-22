package io.github.luminaire1337.propertyvista.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class ModularityTests {
    ApplicationModules modules = ApplicationModules.of(PropertyVistaBackendApiApplication.class);

    @Test
    void verifyModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules)
                .writeDocumentation()
                .writeModulesAsPlantUml();
    }
}