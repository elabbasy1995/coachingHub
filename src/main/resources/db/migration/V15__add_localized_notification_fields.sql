ALTER TABLE notifications
    ADD COLUMN title_en VARCHAR(255),
    ADD COLUMN title_ar VARCHAR(255),
    ADD COLUMN body_en VARCHAR(255),
    ADD COLUMN body_ar VARCHAR(255);

UPDATE notifications
SET title_en = title,
    title_ar = title,
    body_en = body,
    body_ar = body;
