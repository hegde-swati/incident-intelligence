package com.incidentintelligence.order.application;

import com.incidentintelligence.order.domain.OrderResponse;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class OrderApplicationService {

    public OrderResponse getStatus() {
        return new OrderResponse("UP", "order-service is operational", Instant.now().toString());
    }
}
