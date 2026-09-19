CREATE TABLE outbox_events
(
    id             VARCHAR(36) PRIMARY KEY,
    aggregate_type VARCHAR(64)              NOT NULL,
    aggregate_id   VARCHAR(64)              NOT NULL,
    event_type     VARCHAR(64)              NOT NULL,
    payload        JSONB                    NOT NULL,
    status         VARCHAR(16)              NOT NULL DEFAULT 'PENDING',
    retry_count    INT                      NOT NULL DEFAULT 0,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at   TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_payment_outbox_pending ON outbox_events (created_at) WHERE status = 'PENDING';