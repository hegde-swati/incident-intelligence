package com.incidentintelligence.logingestion.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incidentintelligence.logingestion.domain.LogEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogPublisherService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String rawLogsTopic;

    public LogPublisherService(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper,
                               @Value("${app.kafka.topics.raw-logs}") String rawLogsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.rawLogsTopic = rawLogsTopic;
    }

    public void publish(LogEvent event) {
        try {
            kafkaTemplate.send(rawLogsTopic, event.sourceService(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize log event", exception);
        }
    }
}
