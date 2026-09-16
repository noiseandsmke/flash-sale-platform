CREATE TABLE orders
(
    id           VARCHAR(36) PRIMARY KEY,
    user_id      VARCHAR(64)              NOT NULL,
    total_amount NUMERIC(12, 2)           NOT NULL CHECK (total_amount >= 0),
    currency     VARCHAR(3)               NOT NULL,
    status       VARCHAR(32)              NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items
(
    id             BIGSERIAL PRIMARY KEY,
    order_id       VARCHAR(36)              NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    ticket_id      BIGINT                   NOT NULL,
    event_id       BIGINT                   NOT NULL,
    price_amount   NUMERIC(12, 2)           NOT NULL CHECK (price_amount >= 0),
    price_currency VARCHAR(3)               NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);