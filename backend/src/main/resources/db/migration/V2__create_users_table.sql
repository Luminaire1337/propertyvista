CREATE TABLE users
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    email             VARCHAR(100) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    `role`            VARCHAR(255) NOT NULL,
    status            VARCHAR(255) NOT NULL,
    first_name        VARCHAR(50)  NOT NULL,
    last_name         VARCHAR(50)  NOT NULL,
    phone_number      VARCHAR(255) NOT NULL,
    avatar_image_path VARCHAR(255),
    property_points   INT          NOT NULL DEFAULT 7,
    created_at        DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at        DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT uc_users_email UNIQUE (email)
);
