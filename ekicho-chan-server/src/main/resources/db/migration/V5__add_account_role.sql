ALTER TABLE auth_account
    ADD COLUMN role VARCHAR(16) NOT NULL DEFAULT 'USER';

ALTER TABLE auth_account
    ADD CONSTRAINT chk_auth_account_role CHECK (role IN ('USER', 'ADMIN'));
