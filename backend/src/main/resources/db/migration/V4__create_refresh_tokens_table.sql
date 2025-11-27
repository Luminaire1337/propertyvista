CREATE TABLE refresh_tokens
(
    id          UUID PRIMARY KEY,
    user_id     UUID     NOT NULL,
    expiry_date datetime NOT NULL,
    CONSTRAINT fk_refresh_tokens_on_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);