package io.github.luminaire1337.propertyvista.backend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MinioConfig {
    @Value("${propertyvista.storage.url}")
    private String storageUrl;

    @Value("${propertyvista.storage.access-key}")
    private String accessKey;

    @Value("${propertyvista.storage.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(storageUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}
