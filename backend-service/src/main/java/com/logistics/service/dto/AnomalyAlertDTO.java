package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AnomalyAlertDTO {
    private Long id;
    private String anomalyType;
    private String city;
    private String orderId;
    private String courierId;
    private String anomalySeverity;
    private BigDecimal anomalyValue;
    private BigDecimal thresholdValue;
    private String description;
    private String originalTime;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private Boolean isResolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

}