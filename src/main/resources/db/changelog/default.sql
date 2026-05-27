--liquibase formatted sql

--changeset jtt:002-default-otp-config
INSERT INTO otp_config (code_length, ttl_seconds)
SELECT 6, 300
WHERE NOT EXISTS (SELECT 1 FROM otp_config);
--rollback DELETE FROM otp_config;
