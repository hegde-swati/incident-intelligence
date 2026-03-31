package com.incidentintelligence.incidentintelligence.dashboard.application;

import com.incidentintelligence.incidentintelligence.dashboard.domain.AIAnalyzeRequest;
import com.incidentintelligence.incidentintelligence.dashboard.domain.AIAnalyzeResponse;
import com.incidentintelligence.incidentintelligence.dashboard.domain.ServiceLogEntry;
import com.incidentintelligence.incidentintelligence.dashboard.domain.ServiceSummary;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class DashboardApplicationService {

    private static final Map<String, String> SERVICES = Map.ofEntries(
            Map.entry("order-service", "Order Service"),
            Map.entry("payment-service", "Payment Service"),
            Map.entry("inventory-service", "Inventory Service"),
            Map.entry("incident-intelligence-service", "Incident Intelligence Service"),
            Map.entry("ai-service", "AI Analysis Service")
    );

    public List<ServiceSummary> getServices() {
        Instant now = Instant.now();
        List<ServiceSummary> summaries = new ArrayList<>();

        for (Map.Entry<String, String> entry : SERVICES.entrySet()) {
            String id = entry.getKey();
            int seed = Math.abs(Objects.hash(id, now.getEpochSecond() / 10));

            summaries.add(new ServiceSummary(
                    id,
                    entry.getValue(),
                    seed % 17 == 0 ? "ERROR" : "RUNNING",
                    15 + (seed % 70),
                    250 + (seed % 1450),
                    1024 + (seed % 19000),
                    now.toString()
            ));
        }

        return summaries;
    }

    public List<ServiceLogEntry> getLogs(String serviceId) {
        if (!SERVICES.containsKey(serviceId)) {
            return List.of();
        }

        Instant now = Instant.now();
        return List.of(
                new ServiceLogEntry(now.minus(45, ChronoUnit.SECONDS).toString(), "INFO", serviceId + " heartbeat check completed"),
                new ServiceLogEntry(now.minus(30, ChronoUnit.SECONDS).toString(), "INFO", "Latency stable at 42ms for " + serviceId),
                new ServiceLogEntry(now.minus(20, ChronoUnit.SECONDS).toString(), "WARN", "Retry observed while calling dependency in " + serviceId),
                new ServiceLogEntry(now.minus(10, ChronoUnit.SECONDS).toString(), "ERROR", "Transient upstream timeout in " + serviceId),
                new ServiceLogEntry(now.toString(), "INFO", "Auto-recovery succeeded for " + serviceId)
        );
    }

    public AIAnalyzeResponse analyze(AIAnalyzeRequest request) {
        List<ServiceLogEntry> logs = request.logs() == null ? List.of() : request.logs();
        int errorCount = (int) logs.stream().filter(log -> "ERROR".equalsIgnoreCase(log.level())).count();
        if (errorCount > 0) {
            return new AIAnalyzeResponse(
                    "Intermittent dependency timeout detected.",
                    "HIGH",
                    "Inspect downstream endpoint latency and configure exponential backoff for retries."
            );
        }

        return new AIAnalyzeResponse(
                "No critical issues found in recent logs.",
                "LOW",
                "Continue monitoring and keep alert thresholds tuned to traffic patterns."
        );
    }
}
