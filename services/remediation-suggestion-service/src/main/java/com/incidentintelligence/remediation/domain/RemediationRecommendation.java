package com.incidentintelligence.remediation.domain;

import java.time.Instant;
import java.util.List;

public record RemediationRecommendation(
        String incidentId,
        String severity,
        String recommendationSummary,
        List<String> playbookSteps,
        Instant generatedAt
) {
}
