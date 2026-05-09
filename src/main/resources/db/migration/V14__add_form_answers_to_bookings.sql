ALTER TABLE bookings
    ADD COLUMN form_challenge VARCHAR(1000),
    ADD COLUMN form_why_important VARCHAR(1000),
    ADD COLUMN form_commitment INTEGER,
    ADD COLUMN form_open_to_help BOOLEAN,
    ADD COLUMN form_tried_before VARCHAR(1000);
