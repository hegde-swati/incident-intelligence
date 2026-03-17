package com.incidentintelligence.anomalydetection.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentintelligence.anomalydetection.domain.AnomalyEvent;
import com.incidentintelligence.anomalydetection.domain.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Locale;

@Component
public class AnomalyDetectionConsumer {
    private static final Logger logger = LoggerFactory.getLogger(AnomalyDetectionConsumer.class);
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String anomalyTopic;

    public AnomalyDetectionConsumer(ObjectMapper objectMapper,
                                    KafkaTemplate<String, String> kafkaTemplate,
                                    @Value("${app.kafka.topics.anomalies}") String anomalyTopic) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.anomalyTopic = anomalyTopic;
    }

    @KafkaListener(topics = "${app.kafka.topics.raw-logs}", groupId = "anomaly-detection")
    public void detect(String payload) throws JsonProcessingException {
        LogEvent logEvent = objectMapper.readValue(payload, LogEvent.class);
        double score = score(logEvent);
        if (score < 0.65) {
            return;
        }
        AnomalyEvent anomalyEvent = new AnomalyEvent(
                logEvent.sourceService(),
                logEvent.message(),
                logEvent.level(),
                score,
                Instant.now());
        kafkaTemplate.send(anomalyTopic, anomalyEvent.sourceService(), objectMapper.writeValueAsString(anomalyEvent));
        logger.info("Published anomaly event with score={}", score);
    }

    private double score(LogEvent logEvent) {
        String normalized = logEvent.message().toLowerCase(Locale.ROOT);
        double score = 0.10;
        if ("error".equalsIgnoreCase(logEvent.level())) {
            score += 0.45;
        }
        if (normalized.contains("timeout") || normalized.contains("outofmemory") || normalized.contains("failed")) {
            score += 0.35;
        }
        if (normalized.contains("database") || normalized.contains("payment") || normalized.contains("latency")) {
            score += 0.20;
        }
        return Math.min(score, 0.99);
    }
}
