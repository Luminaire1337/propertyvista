CREATE TABLE verification_tokens
(
    id          UUID PRIMARY KEY,
    user_id     UUID     NOT NULL,
    expiry_date datetime NOT NULL,
    CONSTRAINT uc_verification_tokens_user_id UNIQUE (user_id),
    CONSTRAINT fk_verification_tokens_on_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);