package com.daimlertrucksasia.it.dsc.healthmonitor.Health.controller;




import com.daimlertrucksasia.it.dsc.healthmonitor.Health.services.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@RestController                     // Spring MVC controller
@RequestMapping("/healthz")         // same path as before
@RequiredArgsConstructor            // Lombok – constructor injection only
public class HealthController {

    /** Maps domain‐specific status → HTTP status code */
    private static final Map<HealthCheckService.Status, HttpStatus> STATUS_MAPPING;
    static {
        Map<HealthCheckService.Status, HttpStatus> tmp = new EnumMap<>(HealthCheckService.Status.class);
        tmp.put(HealthCheckService.Status.FAILED,    HttpStatus.INTERNAL_SERVER_ERROR);
        tmp.put(HealthCheckService.Status.HEALTHY,   HttpStatus.OK);
        tmp.put(HealthCheckService.Status.UNHEALTHY, HttpStatus.CONFLICT);
        STATUS_MAPPING = Map.copyOf(tmp);           // immutable in Java 17
    }

    private final HealthCheckService svc;            // injected by Spring

    /**
     * Kubernetes‑style health check.
     * All query parameters are forwarded to {@link HealthCheckService#checkHealthRest}.
     */
    @GetMapping
    public ResponseEntity<Void> check(
            @RequestParam MultiValueMap<String, String> queryParams) {

        HealthCheckService.Status status =
                Objects.requireNonNullElse(
                        svc.checkHealthRest(queryParams),
                        HealthCheckService.Status.FAILED);

        return ResponseEntity.status(STATUS_MAPPING.get(status)).build();
    }
}

