CREATE TABLE payments
(
    id                       UUID PRIMARY KEY,
    stripe_payment_intent_id VARCHAR(255)  NULL,
    user_id                  UUID          NOT NULL,
    amount                   FLOAT         NOT NULL,
    status                   VARCHAR(255)  NOT NULL,
    failure_reason           VARCHAR(1000) NULL,
    created_at               DATETIME      NOT NULL,
    updated_at               DATETIME      NOT NULL,
    CONSTRAINT uc_payments_stripe_payment_intent UNIQUE (stripe_payment_intent_id),
    CONSTRAINT fk_payments_on_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_payments_stripe_payment_intent_id (stripe_payment_intent_id),
    INDEX idx_payments_user_id (user_id)
);