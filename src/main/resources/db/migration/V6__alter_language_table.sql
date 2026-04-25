ALTER TABLE languages
ADD COLUMN name_en VARCHAR(100),
ADD COLUMN name_ar VARCHAR(100);

UPDATE languages
SET name_en = name;

UPDATE languages SET name_ar = 'الإنجليزية' WHERE code = 'EN';
UPDATE languages SET name_ar = 'الإيطالية' WHERE code = 'IT';
UPDATE languages SET name_ar = 'العربية' WHERE code = 'AR';
UPDATE languages SET name_ar = 'الإسبانية' WHERE code = 'ES';

ALTER TABLE languages
ALTER COLUMN name_en SET NOT NULL,
ALTER COLUMN name_ar SET NOT NULL;

ALTER TABLE languages
DROP COLUMN name;