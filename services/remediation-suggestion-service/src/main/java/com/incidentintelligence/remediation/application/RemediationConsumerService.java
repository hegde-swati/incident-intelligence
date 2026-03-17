package com.incidentintelligence.remediation.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentintelligence.remediation.domain.ClassifiedIncidentEvent;
import com.incidentintelligence.remediation.domain.RemediationRecommendation;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RemediationConsumerService {
    private final ObjectMapper objectMapper;
    private final AtomicReference<RemediationRecommendation> latestRecommendation = new AtomicReference<>();

    public RemediationConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topics.classified}", groupId = "remediation-suggestion")
    public void suggest(String payload) throws JsonProcessingException {
        ClassifiedIncidentEvent incident = objectMapper.readValue(payload, ClassifiedIncidentEvent.class);

        List<String> steps = switch (incident.category()) {
            case "DATABASE" -> List.of("Inspect DB connection pool metrics.", "Run slow query analysis.", "Scale read replicas if saturation persists.");
            case "PAYMENT" -> List.of("Check payment gateway status page.", "Enable retry with exponential backoff.", "Route traffic to backup payment provider.");
            case "PERFORMANCE" -> List.of("Review CPU/memory pressure.", "Increase pod replicas.", "Throttle non-critical batch jobs.");
            default -> List.of("Review recent deployments.", "Collect correlated traces/logs.", "Escalate to on-call engineer.");
        };

        latestRecommendation.set(new RemediationRecommendation(
                incident.incidentId(),
                incident.severity(),
                "Suggested remediation for " + incident.category() + " incident in " + incident.sourceService(),
                steps,
                Instant.now()));
    }

    public RemediationRecommendation latestRecommendation() {
        return latestRecommendation.get();
    }
}
