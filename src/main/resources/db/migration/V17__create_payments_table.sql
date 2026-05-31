CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    provider_payment_intent_id VARCHAR(255),
    provider_checkout_session_id VARCHAR(255),
    provider_charge_id VARCHAR(255),
    provider_refund_id VARCHAR(255),
    provider_event_id VARCHAR(255),
    failure_reason VARCHAR(1000),
    paid_at timestamp(6) with time zone,
    refunded_at timestamp(6) with time zone,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_date timestamp(6) NOT NULL,
    created_by VARCHAR(255),
    updated_date timestamp(6),
    updated_by VARCHAR(255),

    CONSTRAINT fk_payments_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
    CONSTRAINT uk_payments_provider_payment_intent
        UNIQUE (provider_payment_intent_id),
    CONSTRAINT uk_payments_provider_checkout_session
        UNIQUE (provider_checkout_session_id),
    CONSTRAINT uk_payments_provider_event
        UNIQUE (provider_event_id)
);

CREATE INDEX idx_payments_booking_id ON payments (booking_id);
CREATE INDEX idx_payments_provider_status ON payments (provider, status);
CREATE INDEX idx_payments_provider_event_id ON payments (provider, provider_event_id);
