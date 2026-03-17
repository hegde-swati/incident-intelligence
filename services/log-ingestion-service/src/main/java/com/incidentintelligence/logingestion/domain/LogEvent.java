package com.incidentintelligence.logingestion.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;

public record LogEvent(
        @NotBlank String sourceService,
        @NotBlank String environment,
        @NotBlank String level,
        @NotBlank String message,
        @NotNull Instant timestamp,
        Map<String, String> metadata
) {
}
