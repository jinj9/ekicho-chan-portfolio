ALTER TABLE account_station_collection
    ADD COLUMN first_obtained_method VARCHAR(16) NOT NULL DEFAULT 'UNKNOWN',
    ADD COLUMN last_obtained_method VARCHAR(16) NOT NULL DEFAULT 'UNKNOWN';

ALTER TABLE account_station_collection
    ADD CONSTRAINT ck_collection_first_method
        CHECK (first_obtained_method IN ('GACHA', 'LOCATION', 'UNKNOWN')),
    ADD CONSTRAINT ck_collection_last_method
        CHECK (last_obtained_method IN ('GACHA', 'LOCATION', 'UNKNOWN'));
