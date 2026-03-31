package com.incidentintelligence.incidentintelligence.dashboard.web;

import com.incidentintelligence.incidentintelligence.dashboard.application.DashboardApplicationService;
import com.incidentintelligence.incidentintelligence.dashboard.domain.AIAnalyzeRequest;
import com.incidentintelligence.incidentintelligence.dashboard.domain.AIAnalyzeResponse;
import com.incidentintelligence.incidentintelligence.dashboard.domain.ServiceLogEntry;
import com.incidentintelligence.incidentintelligence.dashboard.domain.ServiceSummary;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardApplicationService dashboardApplicationService;

    public DashboardController(DashboardApplicationService dashboardApplicationService) {
        this.dashboardApplicationService = dashboardApplicationService;
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceSummary>> getServices() {
        return ResponseEntity.ok(dashboardApplicationService.getServices());
    }

    @GetMapping("/services/{id}/logs")
    public ResponseEntity<List<ServiceLogEntry>> getServiceLogs(@PathVariable("id") String id) {
        return ResponseEntity.ok(dashboardApplicationService.getLogs(id));
    }

    @GetMapping(path = "/services/{id}/logs/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamServiceLogs(@PathVariable("id") String id) {
        return dashboardApplicationService.streamLogs(id);
    }

    @PostMapping("/ai/analyze")
    public ResponseEntity<AIAnalyzeResponse> analyzeLogs(@RequestBody AIAnalyzeRequest request) {
        return ResponseEntity.ok(dashboardApplicationService.analyze(request));
    }
}
