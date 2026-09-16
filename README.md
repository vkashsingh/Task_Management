# DevOps CI/CD Pipeline — Task Management Service

An end-to-end CI/CD pipeline for a Spring Boot REST API using Jenkins, Docker, Amazon ECR, AWS Systems Manager (SSM), and Amazon EC2.

The pipeline automatically builds, tests, containerizes, publishes, deploys, and verifies the application.

---

## CI/CD Pipeline

```text
GitHub
   │
   ▼
Jenkins EC2
   │
   ├── Checkout
   ├── Maven Build & Test
   ├── Docker Build
   └── Push Image to ECR
         │
         ▼
      Amazon ECR
      (taskmanagement:BUILD_NUMBER)
         │
         │  AWS SSM (no SSH required)
         ▼
Application EC2
   │
   ├── Pull Image from ECR
   ├── Replace Container
   ├── Health Check ✅
   └── Docker Cleanup
         │
         ▼
    PostgreSQL Container
    (management-task-network)
```

---

## AWS Components

| Component | Service | Role |
| :--- | :--- | :--- |
| CI/CD Server | EC2 (Jenkins) | Builds, tests, and pushes Docker images |
| Application Host | EC2 (App) | Runs containerized Spring Boot app + PostgreSQL |
| Container Registry | Amazon ECR | Stores versioned Docker images |
| Remote Execution | AWS SSM | Deploys to App EC2 without SSH keys |
| Authentication | IAM Roles | Passwordless access to ECR and SSM |

---

## Pipeline Stages (`Jenkinsfile`)

| Stage | Description |
| :--- | :--- |
| `Checkout` | Pulls latest code from GitHub |
| `Maven Build & Test` | Runs `mvn clean test package` |
| `Docker Build` | Multi-stage build: JDK 21 → Alpine JRE 21 image |
| `ECR Login & Push` | Tags image with `${BUILD_NUMBER}` and pushes to ECR |
| `Deploy via SSM` | Remotely pulls image, restarts container, runs health check |

---

## Verification

- **Health Check:** `http://<APP_EC2_IP>:8080/actuator/health`
- **Swagger UI:** `http://<APP_EC2_IP>:8080/swagger-ui.html`