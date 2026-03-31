package com.incidentintelligence.incidentintelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IncidentIntelligenceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IncidentIntelligenceServiceApplication.class, args);
    }
}
