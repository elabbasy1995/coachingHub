CREATE TABLE languages (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO languages (code, name, deleted) VALUES
('EN', 'English', FALSE),
('IT', 'Italian', FALSE),
('AR', 'Arabic', FALSE),
('ES', 'Spanish', FALSE);