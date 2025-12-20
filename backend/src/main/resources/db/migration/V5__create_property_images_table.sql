CREATE TABLE property_images
(
    id          UUID PRIMARY KEY,
    property_id UUID         NOT NULL,
    image_path  VARCHAR(255) NOT NULL,
    alt_text    VARCHAR(255) NULL,
    is_primary  BIT(1)       NOT NULL,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,
    CONSTRAINT uc_property_images_image_path UNIQUE (image_path),
    CONSTRAINT fk_property_images_on_property FOREIGN KEY (property_id) REFERENCES properties (id),
    INDEX idx_property_images_property_id (property_id)
);
