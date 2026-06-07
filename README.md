# Inventory backend

[![Quality Gate Status](https://sonarqube.pm4.init-lab.ch/api/project_badges/measure?project=ch.portami%3Ainventario-backend&metric=alert_status&token=sqb_138eea231f119f9f1b63467ceca81f50cab123ce)](https://sonarqube.pm4.init-lab.ch/dashboard?id=ch.portami%3Ainventario-backend)
[![Coverage](https://sonarqube.pm4.init-lab.ch/api/project_badges/measure?project=ch.portami%3Ainventario-backend&metric=coverage&token=sqb_138eea231f119f9f1b63467ceca81f50cab123ce)](https://sonarqube.pm4.init-lab.ch/dashboard?id=ch.portami%3Ainventario-backend)

Backend service for the Portami Inventario application, built with Java 25 and Spring Boot 4.

## Tech Stack

| Layer            | Technology                  |
|------------------|-----------------------------|
| Language         | Java 25                     |
| Framework        | Spring Boot 4.0.4           |
| Database         | MariaDB                     |
| Schema migration | Flyway                      |
| Persistence      | Spring Data JPA / Hibernate |
| Build Tool       | Maven (Maven Wrapper)       |
| Tests            | JUnit 5, Testcontainers     |
| Containerization | Docker (eclipse-temurin:25) |
| CI/CD            | GitHub Actions              |
| Code Quality     | SonarQube                   |

## Prerequisites

- Java 25 (Temurin recommended)
- Maven 3.9+ or the included `./mvnw` wrapper
- Docker — required both for containerized runs and for the test suite (Testcontainers starts a real
  MariaDB)

## Getting Started

### Run locally

Before starting the application locally, make sure the required environment files are in place:

1. Create a `.env` file inside the `docker/` directory using the structure from `docker/.env.sample`.
2. Create a `.env` file in the project root using the structure from the root `.env.sample`.

After that, start the Docker services defined in `docker/docker-compose.yaml` (this brings up MariaDB).

We recommend starting the application using the saved run configuration, because the `.env` file is already assigned
there.

The application starts on **http://localhost:8080**.

To start with demo data and the API documentation enabled, run with the `dev` profile (see
[Profiles](#profiles)):

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

### Configuration

The application is configured through environment variables. The root `.env` holds the connection the
application uses; `docker/.env` holds the credentials MariaDB is created with. Keep the two in sync.

| Variable                     | File         | Description                              |
|------------------------------|--------------|------------------------------------------|
| `SPRING_DATASOURCE_URL`      | `.env`       | JDBC URL, e.g. `jdbc:mariadb://localhost:3306/inventario` |
| `SPRING_DATASOURCE_USERNAME` | `.env`       | Database user the application connects as |
| `SPRING_DATASOURCE_PASSWORD` | `.env`       | Password for that user                   |
| `MARIADB_DATABASE`           | `docker/.env`| Database created by the MariaDB container |
| `MARIADB_USER`               | `docker/.env`| Application user created by the container |
| `MARIADB_PASSWORD`           | `docker/.env`| Password for the application user        |
| `MARIADB_ROOT_PASSWORD`      | `docker/.env`| MariaDB root password                    |

### Profiles

| Profile        | Schema           | Reference data | Demo data | API docs (Swagger UI) |
|----------------|------------------|----------------|-----------|-----------------------|
| _default_      | Flyway, validated | yes            | no        | disabled              |
| `dev`          | Flyway, validated | yes            | yes       | enabled               |

Activate a profile with `SPRING_PROFILES_ACTIVE=dev`. In the `dev` profile the OpenAPI UI is available
at **http://localhost:8080/swagger-ui.html**.

### Run tests

```bash
./mvnw verify
```

The integration tests spin up a disposable MariaDB via Testcontainers, so Docker must be running. They
do not touch your local database.

### Build a JAR

```bash
./mvnw package -DskipTests
java -jar target/*.jar
```

### Build and run with Docker

```bash
docker build -t portami-inventory-backend .
docker run -p 8080:8080 portami-inventory-backend
```

## Database schema and migrations (Flyway)

The database schema is owned by **Flyway**, not by Hibernate. `spring.jpa.hibernate.ddl-auto` is set to
`validate`, so on startup Hibernate only checks that the entities match the migrated schema and never
changes it. This makes schema changes explicit, versioned and reviewable, and turns any drift between
entities and schema into a fast startup failure.

Migrations live in `src/main/resources`:

- `db/migration` — applied in **every** environment:
  - `V1__baseline_schema.sql` — the full schema.
  - `V2__reference_data.sql` — reference data the application needs everywhere (felt types, suppliers,
    storage locations).
- `db/migration-dev` — applied **only** in the `dev` profile (a sibling directory, deliberately not
  nested under `db/migration`, which Flyway scans recursively). Contains demo data: the felt
  catalogue, products, and fabricated offer history.

Guidelines:

- To change the schema, add a new `V<n>__description.sql` migration. **Never edit an already-applied
  migration** — Flyway validates checksums and will fail if a previously applied file changes.
- The project does not set `spring.flyway.baseline-on-migrate`, so Flyway expects to manage the
  database from the start: a fresh database, or one created by these migrations. Running against an
  existing schema that has tables but no Flyway history table will fail on startup.
- When adopting Flyway on a database that already contains tables (for example one previously managed
  by `ddl-auto`), set `spring.flyway.baseline-on-migrate=true` so the existing schema is baselined as
  `V1` instead of failing. If that database already holds the reference data, also set
  `spring.flyway.baseline-version` so the data-seeding migration is not re-applied. For a local
  development database the simplest path is to drop and recreate it and let the migrations rebuild it.

## Demo and seed data (dev profile)

In the `dev` profile the demo data is created two complementary ways, by nature of the data:

- **SQL migrations** (`db/migration-dev`) for the catalogue and the fabricated offer history. Offers
  carry historical timestamps and terminal states that the normal API cannot produce (created/updated
  timestamps are generated by the persistence layer), so they are seeded as raw SQL.
- **`DevDataSeeder`** for inventory (felt rolls and scrap pieces). It creates them through the real
  services, so batch identifiers and barcodes are generated by the production code path rather than
  hand-written. It is idempotent: it does nothing if any felt roll already exists, so restarts do not
  duplicate data.

## Code Style

This project ships with a shared IntelliJ IDEA code style, checked in under `.idea/codeStyles`
(`Project.xml` plus `codeStyleConfig.xml`, which enables per-project settings). IntelliJ picks it up
automatically when you open the project; confirm it under
`Settings > Editor > Code Style > Scheme: Project`.

Please format with this scheme before committing and do not apply custom formatting settings —
consistency across the team depends on everyone using the provided configuration.

## Code Quality — SonarLint and SonarQube

Install the **SonarLint** plugin in your IDE and connect it to the project's SonarQube server. This gives you real-time
feedback that matches the same rules enforced in CI.

SonarQube Quality Gates are checked automatically on every pull request. **A PR cannot be merged into `main` until all
Quality Gates pass.**

## Branching and Deployment Strategy

Please refer to the team Confluence page for the full branching model and deployment strategy:
[https://portami.atlassian.net/wiki/x/AYCe](https://portami.atlassian.net/wiki/x/AYCe)
