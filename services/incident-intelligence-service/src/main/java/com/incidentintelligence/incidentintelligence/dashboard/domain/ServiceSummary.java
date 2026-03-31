package com.incidentintelligence.incidentintelligence.dashboard.domain;

public record ServiceSummary(
        String id,
        String name,
        String status,
        int cpu,
        int memory,
        int disk,
        String lastUpdated
) {
}
