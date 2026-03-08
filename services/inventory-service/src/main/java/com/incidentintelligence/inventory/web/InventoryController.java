package com.incidentintelligence.inventory.web;

import com.incidentintelligence.inventory.application.InventoryApplicationService;
import com.incidentintelligence.inventory.domain.InventoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory/status")
public class InventoryController {

    private final InventoryApplicationService applicationService;

    public InventoryController(InventoryApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<InventoryResponse> getStatus() {
        return ResponseEntity.ok(applicationService.getStatus());
    }
}
