package com.incidentintelligence.incidentintelligence.dashboard.application;

import com.incidentintelligence.incidentintelligence.dashboard.domain.AIAnalyzeRequest;
import com.incidentintelligence.incidentintelligence.dashboard.domain.AIAnalyzeResponse;
import com.incidentintelligence.incidentintelligence.dashboard.domain.ServiceLogEntry;
import com.incidentintelligence.incidentintelligence.dashboard.domain.ServiceSummary;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class DashboardApplicationService {

    static final Map<String, String> SERVICES = Map.ofEntries(
            Map.entry("order-service", "Order Service"),
            Map.entry("payment-service", "Payment Service"),
            Map.entry("inventory-service", "Inventory Service"),
            Map.entry("incident-intelligence-service", "Incident Intelligence Service"),
            Map.entry("ai-service", "AI Analysis Service")
    );

    public static final Set<String> SERVICE_IDS = SERVICES.keySet();

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

    private final ServiceLogStreamService serviceLogStreamService;

    public DashboardApplicationService(ServiceLogStreamService serviceLogStreamService) {
        this.serviceLogStreamService = serviceLogStreamService;
    }

    public List<ServiceLogEntry> getLogs(String serviceId) {
        if (!SERVICES.containsKey(serviceId)) {
            return List.of();
        }

        return serviceLogStreamService.getBufferedLogs(serviceId);
    }


    public SseEmitter streamLogs(String serviceId) {
        if (!SERVICES.containsKey(serviceId)) {
            return new SseEmitter(1L);
        }

        return serviceLogStreamService.subscribe(serviceId);
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
