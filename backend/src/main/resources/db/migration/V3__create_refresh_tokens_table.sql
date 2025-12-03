CREATE TABLE refresh_tokens
(
    id          UUID PRIMARY KEY,
    token       VARCHAR(255) NOT NULL,
    user_id     UUID         NOT NULL,
    user_agent  VARCHAR(255) NOT NULL,
    expiry_date DATETIME     NOT NULL,
    CONSTRAINT fk_refresh_tokens_on_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_refresh_tokens_token (token),
    INDEX idx_refresh_tokens_user_id (user_id)
);