package com.incidentintelligence.incidentclassification.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentintelligence.incidentclassification.domain.AnomalyEvent;
import com.incidentintelligence.incidentclassification.domain.ClassifiedIncidentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Component
public class IncidentClassificationConsumer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String classifiedTopic;

    public IncidentClassificationConsumer(ObjectMapper objectMapper,
                                          KafkaTemplate<String, String> kafkaTemplate,
                                          @Value("${app.kafka.topics.classified}") String classifiedTopic) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.classifiedTopic = classifiedTopic;
    }

    @KafkaListener(topics = "${app.kafka.topics.anomalies}", groupId = "incident-classification")
    public void classify(String payload) throws JsonProcessingException {
        AnomalyEvent anomaly = objectMapper.readValue(payload, AnomalyEvent.class);
        String message = anomaly.message().toLowerCase(Locale.ROOT);

        String category = message.contains("database") ? "DATABASE"
                : message.contains("payment") ? "PAYMENT"
                : message.contains("latency") || message.contains("timeout") ? "PERFORMANCE"
                : "APPLICATION";

        String severity = anomaly.anomalyScore() > 0.85 ? "CRITICAL"
                : anomaly.anomalyScore() > 0.70 ? "HIGH" : "MEDIUM";

        String rootCause = switch (category) {
            case "DATABASE" -> "Possible connection pool exhaustion or failing query plan.";
            case "PAYMENT" -> "Downstream payment gateway response failures.";
            case "PERFORMANCE" -> "Resource saturation or network latency spike.";
            default -> "Unexpected application behavior requiring operator review.";
        };

        ClassifiedIncidentEvent event = new ClassifiedIncidentEvent(
                UUID.randomUUID().toString(),
                anomaly.sourceService(),
                category,
                severity,
                "RAG summary: " + summarize(anomaly.message(), category),
                rootCause,
                Instant.now());

        kafkaTemplate.send(classifiedTopic, event.incidentId(), objectMapper.writeValueAsString(event));
    }

    private String summarize(String message, String category) {
        return "Incident appears related to " + category + ". Evidence from logs: '" + message + "'.";
    }
}
