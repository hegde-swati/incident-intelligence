package com.incidentintelligence.inventory.application;

import com.incidentintelligence.inventory.domain.InventoryResponse;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class InventoryApplicationService {

    public InventoryResponse getStatus() {
        return new InventoryResponse("UP", "inventory-service is operational", Instant.now().toString());
    }
}
