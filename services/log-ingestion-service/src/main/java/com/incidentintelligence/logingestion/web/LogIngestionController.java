package com.incidentintelligence.logingestion.web;

import com.incidentintelligence.logingestion.application.LogPublisherService;
import com.incidentintelligence.logingestion.domain.LogEvent;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogIngestionController {
    private final LogPublisherService logPublisherService;

    public LogIngestionController(LogPublisherService logPublisherService) {
        this.logPublisherService = logPublisherService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void ingest(@Valid @RequestBody LogEvent logEvent) {
        logPublisherService.publish(logEvent);
    }
}
