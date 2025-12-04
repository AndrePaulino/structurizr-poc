# structurizr-poc

Quarkus service that uses **Structurizr DSL** to document architecture and serve interactive C4 diagrams.

## 🏗️ Architecture

This project automatically generates a static HTML site with C4 diagrams from a DSL file, using the Structurizr CLI during the build.

```
workspace.dsl → Structurizr CLI → Static HTML → Quarkus (/) 
```

## 📋 Prerequisites

- Java 21+
- Maven 3.9+
- Docker (for generating diagrams during the build)

## 🚀 Running

### Local Development

1. **Maven Build** automatically runs the Structurizr CLI via Docker:

```bash
./mvnw clean package
```

2. **Start in dev mode**:
```bash
./mvnw quarkus:dev
```

3. **Access the diagrams**: http://localhost:8080/

### Build and Run with Docker

```bash
docker build -t structurizr-poc . && docker run structurizr-poc
```

### Production Simulation (Docker Compose)

This project includes a `docker-compose.yml` configured to simulate a production Kubernetes environment.

| K8s Feature | Docker Compose Simulation |
|-------------|-------------------------|
| **Liveness Probe** | `healthcheck` with `curl /q/health/live` |
| **Readiness Probe** | Health check on endpoint `/q/health/ready` |
| **Resource Limits** | `deploy.resources.limits: 1 CPU, 512M` |
| **Security Context** | `user: "1000:1000"`, `read_only: true` |
| **Restart Policy** | `restart: unless-stopped` |

**Commands:**

```bash
# Start (in background)
docker-compose up --detach

# View logs
docker-compose logs --follow

# View status and health
docker-compose ps

# Stop
docker-compose down

# With Ingress (nginx) - in another terminal
docker-compose --profile with-ingress up
```

## 📁 Structure

```
src/main/resources/
├── architecture/
│   ├── workspace.dsl      # Architecture definition (Structurizr DSL)
│   └── static/            # Generated HTML site (ignored in git)
├── META-INF/resources/    # Static files served by Quarkus
└── application.properties
```

## ✏️ Editing the Architecture

Edit the `src/main/resources/architecture/workspace.dsl` file to change the diagrams.

Refer to the [Structurizr DSL documentation](https://docs.structurizr.com/dsl) for language reference.

## 🔗 Useful Links

- [Structurizr DSL](https://docs.structurizr.com/dsl)
- [C4 Model](https://c4model.com/)
- [Structurizr CLI](https://docs.structurizr.com/cli)
- [Quarkus](https://quarkus.io/)