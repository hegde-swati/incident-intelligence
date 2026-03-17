# incident-intelligence
AI-Driven Incident Intelligence Platform

## 1) Target architecture (event-driven + AI)
This repository now includes a Kafka-first microservice flow for incident intelligence:

1. **log-ingestion-service** accepts structured logs over HTTP and pushes them to `logs.raw`.
2. **anomaly-detection-service** consumes `logs.raw`, scores anomaly risk, and emits events to `incidents.anomalies`.
3. **incident-classification-service** consumes anomalies, classifies incidents, and produces RAG-style summaries + root-cause hints to `incidents.classified`.
4. **remediation-suggestion-service** consumes classified incidents and generates remediation playbook steps that can be fetched over HTTP.

```text
App/Infra Logs --> log-ingestion-service --> [Kafka: logs.raw]
                                        --> anomaly-detection-service --> [Kafka: incidents.anomalies]
                                        --> incident-classification-service --> [Kafka: incidents.classified]
                                        --> remediation-suggestion-service --> API /api/remediations/latest
```

---

## 2) Project structure
- `services/log-ingestion-service` – REST log intake + Kafka producer
- `services/anomaly-detection-service` – Kafka consumer + anomaly scorer + producer
- `services/incident-classification-service` – Kafka consumer + incident classification + RAG-style summarizer
- `services/remediation-suggestion-service` – Kafka consumer + remediation recommendation API
- `infra/docker/docker-compose.yml` – local infrastructure and service orchestration

---

## 3) Step-by-step build and run

### Step 1: Start infrastructure + services
From repository root:

```bash
cd infra/docker
docker compose up --build
```

Services exposed locally:
- Kafka: `localhost:9092`
- Log ingestion API: `localhost:8084`
- Remediation API: `localhost:8087`

### Step 2: Send sample logs to ingestion service

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

### Step 3: Read generated remediation

```bash
curl http://localhost:8087/api/remediations/latest
```

---

## 4) How AI/RAG is wired (initial baseline)
The classification service includes an initial **RAG-style summarization stub**:
- Retrieves context by category inference from anomaly text.
- Generates incident summary + probable root cause for downstream remediation.

### Next upgrade path (recommended)
1. Add a **vector store** (pgvector, Elasticsearch, or OpenSearch).
2. Store runbooks/postmortems/SOP docs as embeddings.
3. Replace rule-based `summarize(...)` with actual LLM call through Spring AI.
4. Add confidence scoring and human-in-the-loop approval before automated actions.

---

## 5) Suggested implementation roadmap
1. **Foundation**: run Kafka + all four services (done in this scaffold).
2. **Detection quality**: improve anomaly scoring with historical baselines.
3. **Classification quality**: train or prompt-tune category/severity detection.
4. **RAG integration**: attach real knowledge base and LLM service.
5. **Automation guardrails**: approval workflows, rollback hooks, and audit trails.
6. **Observability**: tracing, DLQs, retries, and SLA dashboards.

---

## 6) Topics and contracts
- `logs.raw`: input log events
- `incidents.anomalies`: anomaly events
- `incidents.classified`: classified incident events with summaries/root-cause hints

JSON contracts are defined as Java records in each service's `domain/` package.

---

## 7) Notes
- This is a production-oriented starter scaffold to help you move quickly.
- You can now iteratively enhance each microservice without changing the end-to-end event topology.
