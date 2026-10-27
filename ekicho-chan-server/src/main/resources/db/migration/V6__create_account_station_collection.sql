CREATE TABLE account_station_collection (
    id BIGSERIAL PRIMARY KEY,
    account_id UUID NOT NULL,
    station_id INTEGER NOT NULL,
    first_obtained_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_obtained_at TIMESTAMP WITH TIME ZONE NOT NULL,
    obtained_count INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_collection_account FOREIGN KEY (account_id)
        REFERENCES auth_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_collection_station FOREIGN KEY (station_id)
        REFERENCES station_master(id) ON DELETE RESTRICT,
    CONSTRAINT uk_collection_account_station UNIQUE (account_id, station_id),
    CONSTRAINT ck_collection_obtained_count CHECK (obtained_count >= 1),
    CONSTRAINT ck_collection_obtained_dates CHECK (last_obtained_at >= first_obtained_at)
);

CREATE INDEX idx_collection_account_last_obtained
    ON account_station_collection (account_id, last_obtained_at DESC);

CREATE TRIGGER trg_account_station_collection_set_updated_at
    BEFORE UPDATE ON account_station_collection
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
