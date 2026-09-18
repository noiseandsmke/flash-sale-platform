CREATE TABLE payments
(
    id                    VARCHAR(36) PRIMARY KEY,
    order_id              VARCHAR(36)              NOT NULL,
    user_id               VARCHAR(64)              NOT NULL,
    amount                NUMERIC(12, 2)           NOT NULL CHECK (amount >= 0),
    currency              VARCHAR(3)               NOT NULL,
    payment_method        VARCHAR(32)              NOT NULL,
    status                VARCHAR(32)              NOT NULL,
    transaction_reference VARCHAR(128),
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_order_id ON payments (order_id);
CREATE INDEX idx_payments_status ON payments (status);