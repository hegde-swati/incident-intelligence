#!/usr/bin/env bash
set -euo pipefail

echo "Posting sample log event to log-ingestion-service..."
curl -sS -X POST http://localhost:8084/api/logs \
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
  }' >/dev/null

echo "Waiting for pipeline processing..."
sleep 4

echo "Latest remediation recommendation:"
curl -sS http://localhost:8087/api/remediations/latest | jq . || curl -sS http://localhost:8087/api/remediations/latest
