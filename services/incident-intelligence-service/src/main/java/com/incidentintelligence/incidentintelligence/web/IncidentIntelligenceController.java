package com.incidentintelligence.incidentintelligence.web;

import com.incidentintelligence.incidentintelligence.application.IncidentIntelligenceApplicationService;
import com.incidentintelligence.incidentintelligence.domain.IncidentIntelligenceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents/status")
public class IncidentIntelligenceController {

    private final IncidentIntelligenceApplicationService applicationService;

    public IncidentIntelligenceController(IncidentIntelligenceApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<IncidentIntelligenceResponse> getStatus() {
        return ResponseEntity.ok(applicationService.getStatus());
    }
}
