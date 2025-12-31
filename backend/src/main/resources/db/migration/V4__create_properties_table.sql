CREATE TABLE properties
(
    id          UUID PRIMARY KEY,
    slug        VARCHAR(255)  NOT NULL,
    title       VARCHAR(100)  NOT NULL,
    user_id     UUID          NOT NULL,
    status      VARCHAR(255)  NOT NULL,
    description VARCHAR(5000) NULL,
    price       DOUBLE        NOT NULL,
    city        VARCHAR(255)  NOT NULL,
    area        DOUBLE        NOT NULL,
    rooms       INT UNSIGNED  NOT NULL,
    parking     BIT(1)        NOT NULL,
    expiry_date DATETIME      NOT NULL,
    created_at  DATETIME      NOT NULL,
    updated_at  DATETIME      NOT NULL,
    CONSTRAINT uc_properties_title UNIQUE (slug),
    CONSTRAINT fk_properties_on_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_properties_slug (slug),
    INDEX idx_properties_user_id (user_id)
);