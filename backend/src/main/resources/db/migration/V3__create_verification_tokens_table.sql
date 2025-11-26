CREATE TABLE verification_tokens
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    token       VARCHAR(255) NOT NULL,
    user_id     BIGINT       NOT NULL,
    expiry_date datetime     NOT NULL,
    CONSTRAINT uc_verification_tokens UNIQUE (token, user_id),
    CONSTRAINT fk_verification_tokens_on_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);