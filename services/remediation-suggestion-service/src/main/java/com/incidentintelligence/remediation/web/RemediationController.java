package com.incidentintelligence.remediation.web;

import com.incidentintelligence.remediation.application.RemediationConsumerService;
import com.incidentintelligence.remediation.domain.RemediationRecommendation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/remediations")
public class RemediationController {
    private final RemediationConsumerService remediationConsumerService;

    public RemediationController(RemediationConsumerService remediationConsumerService) {
        this.remediationConsumerService = remediationConsumerService;
    }

    @GetMapping("/latest")
    public ResponseEntity<RemediationRecommendation> latest() {
        RemediationRecommendation recommendation = remediationConsumerService.latestRecommendation();
        if (recommendation == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recommendation);
    }
}
