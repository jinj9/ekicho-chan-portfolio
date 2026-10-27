#!/bin/sh
set -eu

: "${POSTGRES_USER:?POSTGRES_USER is required}"
: "${APP_DB_PASSWORD:?APP_DB_PASSWORD is required}"

psql --set=ON_ERROR_STOP=1 --username "$POSTGRES_USER" \
  --set=app_db_password="$APP_DB_PASSWORD" <<'SQL'
CREATE USER ekicho1 WITH PASSWORD :'app_db_password';
CREATE DATABASE ekicho_chan_local OWNER ekicho1;
SQL

# Create the legacy schema as the application role so Flyway can alter it later.
{
  printf '%s\n' 'SET ROLE ekicho1;'
  cat /seed/01_legacy_schema.sql
} | psql --set=ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname ekicho_chan_local

psql --set=ON_ERROR_STOP=1 --username "$POSTGRES_USER" \
  --dbname ekicho_chan_local --file=/seed/02_station_master_insert.sql

# Image seed data is intentionally optional in the public portfolio.
if [ -f /seed/03_image_insert.sql ]; then
  psql --set=ON_ERROR_STOP=1 --username "$POSTGRES_USER" \
    --dbname ekicho_chan_local --file=/seed/03_image_insert.sql
fi
