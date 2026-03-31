package com.incidentintelligence.incidentintelligence.dashboard.domain;

import java.util.List;

public record AIAnalyzeRequest(String serviceId, List<ServiceLogEntry> logs) {
}
