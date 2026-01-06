package io.github.luminaire1337.propertyvista.backend.task;

import io.github.luminaire1337.propertyvista.backend.entity.Property;
import io.github.luminaire1337.propertyvista.backend.entity.utility.PropertyStatus;
import io.github.luminaire1337.propertyvista.backend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HideExpiredProperties {
    private final PropertyService propertyService;

    // Every hour
    @Scheduled(cron = "0 0 */1 * * *")
    public void hideExpiredProperties() {
        log.info("Running 'hideExpiredProperties' task");

        List<Property> expiredProperties = propertyService.findAllExpiredProperties();
        if (expiredProperties.isEmpty()) {
            return;
        }

        expiredProperties.forEach(property -> property.setStatus(PropertyStatus.EXPIRED));
        propertyService.updatePropertiesInBatch(expiredProperties);
        propertyService.notifyExpiredPropertiesOwners(expiredProperties);
    }
}
