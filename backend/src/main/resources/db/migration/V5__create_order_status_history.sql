CREATE TABLE order_status_history (
    id          UUID        PRIMARY KEY,
    order_id    UUID        NOT NULL REFERENCES orders(id),
    from_status VARCHAR(20),
    to_status   VARCHAR(20) NOT NULL,
    changed_by  VARCHAR(255),
    created_at  TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id);
