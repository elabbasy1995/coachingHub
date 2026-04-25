-- Insert default admin user
INSERT INTO users (email, password, language, enabled)
VALUES (
    'admin@coatchinghub.com',
    '$2a$10$X75lBRcfcY87rB3pg.nlpOwrT5CWTHy2ElEDds5Syh9.d1e/AnTbq',
    'EN',
    true
);

-- Create admin record
INSERT INTO admins (full_name, user_id, created_date, created_by, updated_date, updated_by)
SELECT 'System Admin', id, now(), 'APPLICATION', now(), 'APPLICATION'
FROM users
WHERE email = 'admin@coatchinghub.com';

-- Assign ADMIN role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.email = 'admin@coatchinghub.com';
