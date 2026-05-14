# Project Catalogue

User and project management as a set of Spring Boot microservices.

## Services

| Service | Port | What it does |
|---|---|---|
| `auth-service` | 8083 | Issues JWT tokens for client authentication |
| `user-service` | 8081 | User CRUD, email uniqueness, password hashing |
| `project-service` | 8082 | Projects per user, validates user via user-service |

Each service uses a DDD/ECB layout:

- `domain/` — entities, value objects, repository interfaces
- `boundary/` — use cases / application services
- `controller/` — REST controllers
- `infrastructure/` — JWT, security config, persistence adapters

## Stack

- Java 17
- Spring Boot 3.5
- Spring Security (JWT)
- Spring Data JPA
- Micrometer (Prometheus + OpenTelemetry tracing)
- Lombok
- jjwt
- H2 (local dev), PostgreSQL (cluster)

## Other repos

- `project-catalogue-infra` — Kind cluster, ArgoCD, Prometheus, Loki, PostgreSQL
- `project-catalogue-kubernetes` — Helm charts, Grafana dashboards, alert rules

## Running locally

```bash
cd services/auth-service && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
cd services/user-service && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
cd services/project-service && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Or `docker compose up --build`.

## Tests

```bash
cd services/auth-service && ./mvnw test
cd services/user-service && ./mvnw test
cd services/project-service && ./mvnw test
```

## API

Each service exposes Swagger UI at `/swagger-ui.html`:

| Service | Local | Dev (cluster) | Prod (cluster) |
|---|---|---|---|
| auth-service | http://localhost:8083/swagger-ui.html | http://localhost:19083/swagger-ui.html | http://localhost:19183/swagger-ui.html |
| user-service | http://localhost:8081/swagger-ui.html | http://localhost:19081/swagger-ui.html | http://localhost:19181/swagger-ui.html |
| project-service | http://localhost:8082/swagger-ui.html | http://localhost:19082/swagger-ui.html | http://localhost:19182/swagger-ui.html |

| Method | Endpoint | Auth |
|---|---|---|
| `POST` | `/auth/token` | — |
| `POST` | `/api/v1/users` | USER, ADMIN |
| `GET` | `/api/v1/users/{id}` | USER, ADMIN |
| `PUT` | `/api/v1/users/{id}` | USER, ADMIN |
| `DELETE` | `/api/v1/users/{id}` | ADMIN |
| `POST` | `/api/v1/users/{userId}/projects` | ADMIN |
| `GET` | `/api/v1/users/{userId}/projects` | USER, ADMIN |
| `DELETE` | `/api/v1/users/{userId}/projects/{projectId}` | ADMIN |

