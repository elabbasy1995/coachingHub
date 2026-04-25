-- 1️⃣ Add column as nullable first (safe for existing data)
ALTER TABLE coachees
ADD COLUMN active BOOLEAN;

-- 2️⃣ Update existing data (existing coachees are active)
UPDATE coachees
SET active = TRUE
WHERE active IS NULL;

-- 3️⃣ Enforce NOT NULL constraint
ALTER TABLE coachees
ALTER COLUMN active SET NOT NULL;

-- 4️⃣ Set default value for future inserts
ALTER TABLE coachees
ALTER COLUMN active SET DEFAULT FALSE;