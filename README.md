# Inventory backend
[![Quality Gate Status](https://sonarqube.pm4.init-lab.ch/api/project_badges/measure?project=inventario-backend&metric=alert_status&token=sqb_74624c93225bba20767b51b3364c204516d00bb0)](https://sonarqube.pm4.init-lab.ch/dashboard?id=inventario-backend)
[![Coverage](https://sonarqube.pm4.init-lab.ch/api/project_badges/measure?project=inventario-backend&metric=coverage&token=sqb_74624c93225bba20767b51b3364c204516d00bb0)](https://sonarqube.pm4.init-lab.ch/dashboard?id=inventario-backend)

Backend service for the Portami Inventario application, built with Java 25 and Spring Boot 4.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.3 |
| Build Tool | Maven (Maven Wrapper) |
| Containerization | Docker (eclipse-temurin:25) |
| CI/CD | GitHub Actions |
| Code Quality | SonarQube |

## Prerequisites

- Java 25 (Temurin recommended)
- Maven 3.9+ or use the included `./mvnw` wrapper
- Docker (for containerized runs)

## Getting Started

### Run locally

```bash
./mvnw spring-boot:run
```

The application starts on **http://localhost:8080**.

### Run tests

```bash
./mvnw verify
```

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

## Code Style

This project ships with a code style configuration. Please make sure your IDE is set up to use it before writing any code. Do not apply custom formatting settings — consistency across the team depends on everyone using the provided configuration.

## Code Quality — SonarLint & SonarQube

Install the **SonarLint** plugin in your IDE and connect it to the project's SonarQube server. This gives you real-time feedback that matches the same rules enforced in CI.

SonarQube Quality Gates are checked automatically on every pull request. **A PR cannot be merged into `main` until all Quality Gates pass.**

## Branching & Deployment Strategy

Please refer to the team Confluence page for the full branching model and deployment strategy:
[https://portami.atlassian.net/wiki/x/AYCe](https://portami.atlassian.net/wiki/x/AYCe)
