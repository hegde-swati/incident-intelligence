package com.incidentintelligence.payment.application;

import com.incidentintelligence.payment.domain.PaymentResponse;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class PaymentApplicationService {

    public PaymentResponse getStatus() {
        return new PaymentResponse("UP", "payment-service is operational", Instant.now().toString());
    }
}
