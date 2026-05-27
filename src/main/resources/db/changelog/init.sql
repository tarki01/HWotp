--liquibase formatted sql

--changeset jtt:001-create-users
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    login           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    email           VARCHAR(255),
    phone           VARCHAR(50),
    telegram_chat_id VARCHAR(100)
);
--rollback DROP TABLE IF EXISTS users;

--changeset jtt:001-create-otp-config
CREATE TABLE IF NOT EXISTS otp_config (
    id          BIGSERIAL PRIMARY KEY,
    code_length INT NOT NULL DEFAULT 6,
    ttl_seconds INT NOT NULL DEFAULT 300
);
--rollback DROP TABLE IF EXISTS otp_config;

--changeset jtt:001-create-otp-codes
CREATE TABLE IF NOT EXISTS otp_codes (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    operation_id VARCHAR(255) NOT NULL,
    code         VARCHAR(20)  NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'USED')),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    expires_at   TIMESTAMP    NOT NULL
);
--rollback DROP TABLE IF EXISTS otp_codes;
