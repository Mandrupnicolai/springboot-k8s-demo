# springboot-k8s-demo

> A production-grade Spring Boot 3.3 REST API containerised with Docker and deployed to Kubernetes via Helm — built to demonstrate clean architecture, CI/CD best practices, and cloud-native observability.

[![CI/CD](https://github.com/Mandrupnicolai/springboot-k8s-demo/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/Mandrupnicolai/springboot-k8s-demo/actions/workflows/ci-cd.yml)
[![codecov](https://codecov.io/gh/Mandrupnicolai/springboot-k8s-demo/branch/main/graph/badge.svg)](https://codecov.io/gh/Mandrupnicolai/springboot-k8s-demo)
[![Java](https://img.shields.io/badge/Java-21-007396?logo=java)](https://adoptium.net)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-ready-326CE5?logo=kubernetes)](https://kubernetes.io)
[![Helm](https://img.shields.io/badge/Helm-chart-0F1689?logo=helm)](https://helm.sh)
[![Docker](https://img.shields.io/badge/Docker-distroless-2496ED?logo=docker)](https://github.com/Mandrupnicolai/springboot-k8s-demo/pkgs/container/springboot-k8s-demo)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql)](https://www.postgresql.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Last Commit](https://img.shields.io/github/last-commit/Mandrupnicolai/springboot-k8s-demo)](https://github.com/Mandrupnicolai/springboot-k8s-demo/commits/main)
[![Open Issues](https://img.shields.io/github/issues/Mandrupnicolai/springboot-k8s-demo)](https://github.com/Mandrupnicolai/springboot-k8s-demo/issues)

---

## Contents

- [Features](#features)
- [Architecture](#architecture)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Kubernetes & Helm deployment](#kubernetes--helm-deployment)
- [CI/CD pipeline](#cicd-pipeline)
- [API reference](#api-reference)
- [Contributing](#contributing)

---

## Features

- **REST CRUD API** — Task management with full create / read / update / delete operations
- **PostgreSQL** via Spring Data JPA with Flyway schema migrations
- **Actuator** health probes wired to Kubernetes liveness / readiness endpoints
- **Multi-stage distroless Docker image** — minimal attack surface, non-root runtime
- **Helm chart** — fully parameterised, no hardcoded values
- **GitHub Actions CI/CD** — build ? test ? coverage gate ? Docker push (GHCR) ? Helm lint
- **JaCoCo** line coverage enforced at = 80 % in CI
- **Codecov** integration for PR coverage comments

---

## Architecture
┌──────────┐     HTTP     ┌─────────────────────────────────────────┐
│  Client  │ ──────────▶  │  Ingress (nginx)                        │
└──────────┘              └───────────────┬─────────────────────────┘
│
┌───────────────▼─────────────┐
│  TaskController  (:8080)    │
│  /api/v1/tasks              │
└───────────────┬─────────────┘
│
┌───────────────▼─────────────┐
│  TaskService                │
└───────────────┬─────────────┘
│
┌───────────────▼─────────────┐
│  TaskRepository (JPA)       │
└───────────────┬─────────────┘
│
┌───────────────▼─────────────┐
│  PostgreSQL  (:5432)        │
└─────────────────────────────┘
---

## Getting started

### Prerequisites

- Java 21, Maven 3.9+
- Docker 24+
- kubectl + a Kubernetes cluster (local: [kind](https://kind.sigs.k8s.io/) or [minikube](https://minikube.sigs.k8s.io/))
- Helm 3.14+

### Run locally (Docker Compose)

```bash
docker compose up -d          # starts PostgreSQL + the app
curl http://localhost:8080/api/v1/tasks
```

### Run with Maven (H2 in-memory, test profile)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

---

## Configuration

All configuration is supplied via **environment variables** — no values are hardcoded.

| Variable | Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `taskdb` | Database name |
| `DB_USER` | `postgres` | Database user |
| `DB_PASSWORD` | `changeme` | Database password |
| `DB_POOL_SIZE` | `10` | HikariCP max pool size |
| `SERVER_PORT` | `8080` | HTTP port |
| `LOG_LEVEL` | `INFO` | Root log level |

In Kubernetes these are sourced from `ConfigMap` (non-sensitive) and `Secret` (credentials).

---

## Kubernetes & Helm deployment

> **Note:** The deploy stage in CI requires a configured Kubernetes cluster and a `KUBECONFIG` secret.
> The job is currently disabled. See [Enabling deployment](#enabling-deployment) below to activate it.

```bash
# 1. Add your image registry credentials (if private)
kubectl create secret docker-registry ghcr-secret \
  --docker-server=ghcr.io \
  --docker-username=YOUR_GITHUB_USERNAME \
  --docker-password=YOUR_PAT

# 2. Deploy
helm upgrade --install k8s-demo helm/springboot-k8s-demo \
  --set image.tag=latest \
  --set secret.DB_PASSWORD=<your-password> \
  --namespace default \
  --wait

# 3. Check rollout
kubectl rollout status deployment/k8s-demo
```

### Enabling deployment

1. Provision a Kubernetes cluster (e.g. [Civo](https://www.civo.com), [DigitalOcean](https://www.digitalocean.com), or local [kind](https://kind.sigs.k8s.io/)).
2. Download the kubeconfig from your provider.
3. Add it as a GitHub secret named `KUBECONFIG`.
4. Add your database password as a GitHub secret named `DB_PASSWORD`.
5. In `.github/workflows/ci-cd.yml`, remove the `if: false` line from the deploy job.
6. Create a `production` environment under **Settings ? Environments** and enable the manual approval gate.

---

## CI/CD pipeline

| Stage | Trigger | Action |
|---|---|---|
| **Build & Test** | Every push / PR | `mvn verify`, JaCoCo coverage gate (= 80 %) |
| **Docker** | Push to `main` | Multi-arch image ? GHCR |
| **Helm lint** | Every push / PR | `helm lint` + dry-run template |
| **Deploy** | Disabled — requires `KUBECONFIG` secret | `helm upgrade --install` with manual approval gate |

---

## API reference

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/tasks` | List all tasks |
| `GET` | `/api/v1/tasks/{id}` | Get task by id |
| `POST` | `/api/v1/tasks` | Create task |
| `PUT` | `/api/v1/tasks/{id}` | Update task |
| `DELETE` | `/api/v1/tasks/{id}` | Delete task |
| `GET` | `/actuator/health` | Health check |
| `GET` | `/actuator/health/liveness` | Liveness probe |
| `GET` | `/actuator/health/readiness` | Readiness probe |

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
Please open an issue before submitting a large pull request.

---

*MIT License — see [LICENSE](LICENSE)*
