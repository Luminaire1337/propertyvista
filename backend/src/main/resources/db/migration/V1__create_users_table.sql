CREATE TABLE users
(
    id                UUID PRIMARY KEY,
    email             VARCHAR(100) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    role              VARCHAR(255) NOT NULL,
    status            VARCHAR(255) NOT NULL,
    first_name        VARCHAR(50)  NOT NULL,
    last_name         VARCHAR(50)  NOT NULL,
    phone_number      VARCHAR(255) NOT NULL,
    avatar_image_path VARCHAR(255),
    property_points   INT UNSIGNED NOT NULL,
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL,
    CONSTRAINT uc_users_email UNIQUE (email),
    INDEX idx_users_email (email)
);
