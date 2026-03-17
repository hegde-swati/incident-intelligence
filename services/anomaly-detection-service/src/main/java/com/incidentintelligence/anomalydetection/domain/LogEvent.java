package com.incidentintelligence.anomalydetection.domain;

import java.time.Instant;
import java.util.Map;

public record LogEvent(
        String sourceService,
        String environment,
        String level,
        String message,
        Instant timestamp,
        Map<String, String> metadata
) {
}
