# incident-intelligence
AI-Driven Incident Intelligence Platform

This project is a Kafka-driven microservice pipeline for incident detection and response.

## Architecture (implemented)

1. **log-ingestion-service** (`:8084`)
   - Receives log events over REST (`POST /api/logs`)
   - Publishes to Kafka topic `logs.raw`
2. **anomaly-detection-service** (`:8085`)
   - Consumes `logs.raw`
   - Calculates an anomaly score
   - Publishes anomalies to `incidents.anomalies`
3. **incident-classification-service** (`:8086`)
   - Consumes `incidents.anomalies`
   - Classifies category + severity
   - Produces RAG-style summary/root-cause hints to `incidents.classified`
4. **remediation-suggestion-service** (`:8087`)
   - Consumes `incidents.classified`
   - Generates remediation playbook steps
   - Exposes latest recommendation at `GET /api/remediations/latest`

---

## How to run (quick start)

### Prerequisites
- Docker + Docker Compose installed

### 1) Start everything
From repo root:

```bash
cd infra/docker
docker compose up --build
```

This starts:
- Zookeeper
- Kafka
- Postgres
- all 4 incident-intelligence services

### 2) Send a sample incident log
In a new terminal:

```bash
curl -X POST http://localhost:8084/api/logs \
  -H "Content-Type: application/json" \
  -d '{
    "sourceService": "payment-service",
    "environment": "prod",
    "level": "ERROR",
    "message": "Payment timeout while calling gateway and database latency spike",
    "timestamp": "2026-01-10T10:00:00Z",
    "metadata": {
      "traceId": "abc-123",
      "region": "us-east-1"
    }
  }'
```

Expected response: `202 Accepted`

### 3) Fetch the generated remediation recommendation
Wait ~2–5 seconds, then run:

```bash
curl http://localhost:8087/api/remediations/latest
```

Expected response: JSON with:
- `incidentId`
- `severity`
- `recommendationSummary`
- `playbookSteps`
- `generatedAt`

### 4) Stop the stack

```bash
cd infra/docker
docker compose down
```

(Use `docker compose down -v` to also delete database volumes.)

---

## One-command demo script

You can run this helper script after the stack is up:

```bash
./scripts/demo.sh
```

It posts a sample log event, waits a few seconds, then fetches the latest remediation.

---

## Notes about Kafka networking

Compose is configured with:
- internal broker listener for containers: `kafka:29092`
- host listener for local tools: `localhost:9092`

So:
- services in Docker use `KAFKA_BOOTSTRAP_SERVERS=kafka:29092`
- your laptop CLI/tools can use `localhost:9092`

---

## Troubleshooting

### `docker compose up --build` fails on image builds
- verify Docker daemon is running
- check internet access for Maven dependency downloads
- retry build: `docker compose build --no-cache`

### `GET /api/remediations/latest` returns 204
This means no incident has flowed through yet. Make sure:
1. you sent the sample log payload,
2. the log has strong anomaly indicators (`level=ERROR`, includes timeout/failed/database/etc.),
3. services are healthy (`docker compose logs -f`)

### Inspect service logs

```bash
cd infra/docker
docker compose logs -f log-ingestion-service anomaly-detection-service incident-classification-service remediation-suggestion-service
```

---

## Next improvements
- Replace rule-based anomaly scoring with model-driven detection.
- Replace summary stub with true RAG + LLM (Spring AI + vector DB).
- Add persistence for incidents/recommendations.
- Add retries, dead-letter topics, and observability dashboards.
