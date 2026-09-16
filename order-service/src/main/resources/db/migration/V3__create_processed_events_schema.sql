CREATE TABLE processed_events
(
    event_id     VARCHAR(36) PRIMARY KEY,
    event_type   VARCHAR(64)              NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);