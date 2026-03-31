package com.incidentintelligence.incidentintelligence.dashboard.application;

import com.incidentintelligence.incidentintelligence.dashboard.domain.ServiceLogEntry;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class ServiceLogStreamService {

    private static final int MAX_BUFFER_SIZE = 500;

    private final Map<String, Deque<ServiceLogEntry>> serviceBuffers = new ConcurrentHashMap<>();
    private final Map<String, Set<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String serviceId) {
        SseEmitter emitter = new SseEmitter(0L);
        subscribers.computeIfAbsent(serviceId, ignored -> new CopyOnWriteArraySet<>()).add(emitter);

        List<ServiceLogEntry> backlog = getBufferedLogs(serviceId);
        backlog.forEach(logEntry -> sendToEmitter(emitter, logEntry));

        emitter.onCompletion(() -> removeEmitter(serviceId, emitter));
        emitter.onTimeout(() -> removeEmitter(serviceId, emitter));
        emitter.onError(ex -> removeEmitter(serviceId, emitter));

        return emitter;
    }

    public List<ServiceLogEntry> getBufferedLogs(String serviceId) {
        Deque<ServiceLogEntry> buffer = serviceBuffers.get(serviceId);
        if (buffer == null) {
            return List.of();
        }
        synchronized (buffer) {
            return new ArrayList<>(buffer);
        }
    }

    public void publish(String serviceId, ServiceLogEntry entry) {
        Deque<ServiceLogEntry> buffer = serviceBuffers.computeIfAbsent(serviceId, ignored -> new ArrayDeque<>());
        synchronized (buffer) {
            buffer.addLast(entry);
            while (buffer.size() > MAX_BUFFER_SIZE) {
                buffer.removeFirst();
            }
        }

        Set<SseEmitter> emitters = subscribers.getOrDefault(serviceId, Set.of());
        for (SseEmitter emitter : emitters) {
            sendToEmitter(emitter, entry);
        }
    }

    @Scheduled(fixedRate = 3000)
    public void simulateLogs() {
        DashboardApplicationService.SERVICE_IDS.forEach(serviceId -> publish(serviceId, generateLogEntry(serviceId)));
    }

    private ServiceLogEntry generateLogEntry(String serviceId) {
        Random random = ThreadLocalRandom.current();
        int pick = random.nextInt(100);
        String level;
        String message;

        if (pick < 72) {
            level = "INFO";
            message = "Health check OK for " + serviceId;
        } else if (pick < 92) {
            level = "WARN";
            message = "Retry attempt detected for dependency call in " + serviceId;
        } else {
            level = "ERROR";
            message = "Transient upstream timeout while processing request in " + serviceId;
        }

        return new ServiceLogEntry(Instant.now().toString(), level, message);
    }

    private void sendToEmitter(SseEmitter emitter, ServiceLogEntry entry) {
        try {
            emitter.send(SseEmitter.event().name("log").data(entry));
        } catch (IOException ex) {
            emitter.complete();
        }
    }

    private void removeEmitter(String serviceId, SseEmitter emitter) {
        Set<SseEmitter> serviceEmitters = subscribers.get(serviceId);
        if (serviceEmitters != null) {
            serviceEmitters.remove(emitter);
            if (serviceEmitters.isEmpty()) {
                subscribers.remove(serviceId);
            }
        }
    }
}
