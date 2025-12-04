# structurizr-poc

Serviço Quarkus que utiliza **Structurizr DSL** para documentar arquitetura e servir diagramas C4 interativos.

## 🏗️ Arquitetura

Este projeto gera automaticamente um site HTML estático com diagramas C4 a partir de um arquivo DSL, utilizando o Structurizr CLI durante o build.

```
workspace.dsl → Structurizr CLI → HTML Estático → Quarkus (/) 
```

## 📋 Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker (para geração dos diagramas durante o build)

## 🚀 Executando

### Desenvolvimento Local

1. **Build com Maven** executa automaticamente o Structurizr CLI via Docker:

```bash
./mvnw clean package
```

2. **Iniciar em modo dev**:
```bash
./mvnw quarkus:dev
```

3. **Acessar os diagramas**: http://localhost:8080/

### Build e Rode com Docker

```bash
docker build -t structurizr-poc . && docker run structurizr-poc
```

### Simulação de Produção (Docker Compose)

Este projeto inclui um `docker-compose.yml` configurado para simular um ambiente Kubernetes de produção.

| Feature K8s | Simulação Docker Compose |
|-------------|-------------------------|
| **Liveness Probe** | `healthcheck` com `curl /q/health/live` |
| **Readiness Probe** | Health check no endpoint `/q/health/ready` |
| **Resource Limits** | `deploy.resources.limits: 1 CPU, 512M` |
| **Security Context** | `user: "1000:1000"`, `read_only: true` |
| **Restart Policy** | `restart: unless-stopped` |

**Comandos:**

```bash
# Iniciar (em background)
docker-compose up --detach

# Ver logs
docker-compose logs --follow

# Ver status e health
docker-compose ps

# Parar
docker-compose down

# Com Ingress (nginx) - em outro terminal
docker-compose --profile with-ingress up
```

## 📁 Estrutura

```
src/main/resources/
├── architecture/
│   ├── workspace.dsl      # Definição da arquitetura (Structurizr DSL)
│   └── static/            # Site HTML gerado (ignorado no git)
├── META-INF/resources/    # Arquivos estáticos servidos pelo Quarkus
└── application.properties
```

## ✏️ Editando a Arquitetura

Edite o arquivo `src/main/resources/architecture/workspace.dsl` para alterar os diagramas.

Consulte a [documentação do Structurizr DSL](https://docs.structurizr.com/dsl) para referência da linguagem.

## 🔗 Links Úteis

- [Structurizr DSL](https://docs.structurizr.com/dsl)
- [C4 Model](https://c4model.com/)
- [Structurizr CLI](https://docs.structurizr.com/cli)
- [Quarkus](https://quarkus.io/)