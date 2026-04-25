-- Create roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Insert system roles
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'System administrator'),
    ('COACH', 'Approved coach'),
    ('COACHEE', 'Platform coachee');
