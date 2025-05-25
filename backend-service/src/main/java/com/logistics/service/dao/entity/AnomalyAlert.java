package com.logistics.service.dao.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnomalyAlert {
    private Long id;
    private String alertType; // TIME_ANOMALY, DISTANCE_ANOMALY, EFFICIENCY_DROP
    private String city;
    private String orderId;
    private String courierId;
    private String severity; // LOW, MEDIUM, HIGH
    private String description;
    private BigDecimal anomalyValue;
    private BigDecimal thresholdValue;
    private Boolean isResolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}