package com.incidentintelligence.remediation.domain;

import java.time.Instant;

public record ClassifiedIncidentEvent(
        String incidentId,
        String sourceService,
        String category,
        String severity,
        String summary,
        String probableRootCause,
        Instant classifiedAt
) {
}
