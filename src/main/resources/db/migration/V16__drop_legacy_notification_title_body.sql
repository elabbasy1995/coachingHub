UPDATE notifications
SET title_en = COALESCE(title_en, title),
    title_ar = COALESCE(title_ar, title),
    body_en = COALESCE(body_en, body),
    body_ar = COALESCE(body_ar, body);

ALTER TABLE notifications
    DROP COLUMN title,
    DROP COLUMN body;
