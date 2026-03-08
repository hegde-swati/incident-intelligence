package com.incidentintelligence.payment.web;

import com.incidentintelligence.payment.application.PaymentApplicationService;
import com.incidentintelligence.payment.domain.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments/status")
public class PaymentController {

    private final PaymentApplicationService applicationService;

    public PaymentController(PaymentApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<PaymentResponse> getStatus() {
        return ResponseEntity.ok(applicationService.getStatus());
    }
}
