package io.github.luminaire1337.propertyvista.backend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Value("${PROPERTYVISTA_STORAGE_URL}")
    private String storageUrl;

    @Value("${PROPERTYVISTA_STORAGE_ACCESS_KEY}")
    private String accessKey;

    @Value("${PROPERTYVISTA_STORAGE_SECRET_KEY}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(storageUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}
