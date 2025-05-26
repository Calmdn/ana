package com.logistics.service.dao.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AnomalyAlert {
    private Long id;
    private String anomalyType;        // 对应数据库字段 anomaly_type
    private String city;
    private String orderId;            // 对应数据库字段 order_id
    private String courierId;          // 对应数据库字段 courier_id
    private String anomalySeverity;    // 对应数据库字段 anomaly_severity
    private BigDecimal anomalyValue;   // 对应数据库字段 anomaly_value
    private BigDecimal thresholdValue; // 对应数据库字段 threshold_value
    private String description;
    private String originalTime;       // 对应数据库字段 original_time
    private LocalDate analysisDate;    // 对应数据库字段 analysis_date
    private Integer analysisHour;      // 对应数据库字段 analysis_hour
    private Boolean isResolved;        // 对应数据库字段 is_resolved
    private LocalDateTime createdAt;   // 对应数据库字段 created_at
    private LocalDateTime resolvedAt;  // 对应数据库字段 resolved_at
}