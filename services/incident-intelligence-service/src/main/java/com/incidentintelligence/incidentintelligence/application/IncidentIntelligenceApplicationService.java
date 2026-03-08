package com.incidentintelligence.incidentintelligence.application;

import com.incidentintelligence.incidentintelligence.domain.IncidentIntelligenceResponse;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class IncidentIntelligenceApplicationService {

    public IncidentIntelligenceResponse getStatus() {
        return new IncidentIntelligenceResponse("UP", "incident-intelligence-service is operational", Instant.now().toString());
    }
}
