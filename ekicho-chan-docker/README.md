# Docker environment

This directory contains the local and production-oriented Docker Compose definitions used by the portfolio.

## Local startup

1. Copy `.env.example` to `.env`.
2. Replace every `CHANGE_ME` or `REPLACE_...` value with a local-only password.
3. Start the services.

```powershell
Copy-Item .env.example .env
docker compose config --quiet
docker compose up -d
docker compose ps
```

Local endpoints:

- Spring Boot API: `http://127.0.0.1:18080`
- Health check: `http://127.0.0.1:18080/actuator/health`
- Mailpit UI: `http://127.0.0.1:8025`
- PostgreSQL: `127.0.0.1:5432`

## Public sample data

The public repository includes the legacy schema and station master seed. The original image seed is intentionally omitted because it is large and its redistribution rights must be reviewed. Add a properly licensed `seed/03_image_insert.sql` only when image redistribution is permitted.

## Important

- Never commit `.env` or `.env.production`.
- Never run `docker compose down -v` against a database volume you need to keep.
- Production secrets must be supplied outside Git.
