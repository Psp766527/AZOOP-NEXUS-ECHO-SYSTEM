package com.azoopindia.it.asi.traffic.manager.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for RateLimitConfig creation request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateLimitConfigRequest {

    @JsonProperty("clientID")
    private String clientId;

    @JsonProperty("route")
    private String route;

    /**
     * Optional legacy field. Use maxTokensPerWindow if both present.
     */
    @Deprecated
    private long requestsPerMinute;

    @JsonProperty("maxTokensPerWindow")
    private long maxTokensPerWindow;

    @JsonProperty("timeWindow")
    private long timeWindow;

    @JsonProperty("timeUnit")
    private String timeUnit;

    @JsonProperty("burstCapacity")
    private long burstCapacity;

    @JsonProperty("priority")
    private int priority;

    @JsonProperty("expirationDate")
    private LocalDateTime expirationDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("customAttributes")
    private Map<String, String> customAttributes;
}
