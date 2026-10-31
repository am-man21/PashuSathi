-- V1: foundation tables. Never edit this file after it has run; add V2, V3... instead.

CREATE TABLE dairies (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(150) NOT NULL,
    address      VARCHAR(255),
    contact_phone VARCHAR(20),
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    dairy_id              BIGINT       NOT NULL,
    username              VARCHAR(60)  NOT NULL,
    password_hash         VARCHAR(100) NOT NULL,
    full_name             VARCHAR(120) NOT NULL,
    role                  VARCHAR(20)  NOT NULL,
    language_pref         VARCHAR(5)   NOT NULL DEFAULT 'hi',
    must_change_password  BOOLEAN      NOT NULL DEFAULT FALSE,
    enabled               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT fk_users_dairy FOREIGN KEY (dairy_id) REFERENCES dairies(id),
    CONSTRAINT chk_users_role CHECK (role IN ('DAIRY', 'FARMER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_dairy ON users(dairy_id);