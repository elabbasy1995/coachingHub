CREATE TABLE if not exists coaching_industries (
    id BIGSERIAL PRIMARY KEY,
    name_en VARCHAR(150) NOT NULL,
    name_ar VARCHAR(150),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO coaching_industries (name_en, name_ar, deleted) VALUES
('Life Coach',        'مدرب حياة',        FALSE),
('Career Coach',      'مدرب مهني',        FALSE),
('Business Coach',    'مدرب أعمال',       FALSE),
('Relationship Coach','مدرب علاقات',     FALSE),
('Spiritual Coach',   'مدرب روحاني',      FALSE),
('NLP Coach',         'مدرب البرمجة اللغوية العصبية', FALSE);
