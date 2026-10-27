CREATE TABLE auth_account (
    id UUID PRIMARY KEY,
    email VARCHAR(320) NOT NULL,
    normalized_email VARCHAR(320) NOT NULL,
    display_name VARCHAR(30) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    email_verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_auth_account_normalized_email UNIQUE (normalized_email),
    CONSTRAINT ck_auth_account_display_name
        CHECK (CHAR_LENGTH(BTRIM(display_name)) BETWEEN 1 AND 30),
    CONSTRAINT ck_auth_account_status
        CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE', 'LOCKED', 'DISABLED'))
);

CREATE TABLE auth_action_token (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    purpose VARCHAR(24) NOT NULL,
    token_digest BYTEA NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_auth_action_token_account
        FOREIGN KEY (account_id) REFERENCES auth_account(id) ON DELETE CASCADE,
    CONSTRAINT uk_auth_action_token_digest UNIQUE (token_digest),
    CONSTRAINT ck_auth_action_token_purpose
        CHECK (purpose IN ('VERIFY_EMAIL', 'RESET_PASSWORD'))
);

CREATE INDEX ix_auth_action_token_account_purpose
    ON auth_action_token (account_id, purpose);

CREATE INDEX ix_auth_action_token_expires_at
    ON auth_action_token (expires_at);
