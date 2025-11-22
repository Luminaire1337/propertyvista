CREATE TABLE verification_tokens
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    token       VARCHAR(255)          NOT NULL,
    user_id     BIGINT                NOT NULL,
    expiry_date datetime              NOT NULL,
    CONSTRAINT pk_verification_tokens PRIMARY KEY (id),
    CONSTRAINT uc_verification_tokens UNIQUE (token, user_id),
    CONSTRAINT fk_verification_tokens_on_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);