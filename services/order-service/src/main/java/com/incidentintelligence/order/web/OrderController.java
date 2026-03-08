package com.incidentintelligence.order.web;

import com.incidentintelligence.order.application.OrderApplicationService;
import com.incidentintelligence.order.domain.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders/status")
public class OrderController {

    private final OrderApplicationService applicationService;

    public OrderController(OrderApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<OrderResponse> getStatus() {
        return ResponseEntity.ok(applicationService.getStatus());
    }
}
