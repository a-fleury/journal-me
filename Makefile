db-up:
	docker compose up -d journalme-db

db-migrate:
	docker compose run --rm flyway

db-reset:
	docker compose down -v
	docker compose up -d journalme-db
	docker compose run --rm flyway
