-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    language VARCHAR(20),
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Admins table
CREATE TABLE admins (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255),
    user_id BIGINT NOT NULL UNIQUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_date timestamp(6) not null,
    created_by VARCHAR(255),
    updated_date timestamp(6),
    updated_by VARCHAR(255),

    CONSTRAINT fk_admin_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);
