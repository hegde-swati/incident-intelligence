package com.incidentintelligence.incidentclassification.domain;

import java.time.Instant;

public record AnomalyEvent(
        String sourceService,
        String message,
        String level,
        double anomalyScore,
        Instant observedAt
) {
}
