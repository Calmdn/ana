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

    // 计算属性
    public String getSeverityLabel() {
        if (anomalySeverity == null) return "未知";
        switch (anomalySeverity.toUpperCase()) {
            case "HIGH": return "高风险";
            case "MEDIUM": return "中风险";
            case "LOW": return "低风险";
            default: return "未知";
        }
    }

    public String getAnomalyTypeDescription() {
        if (anomalyType == null) return "未知异常";
        switch (anomalyType.toUpperCase()) {
            case "TIME_ANOMALY": return "时间异常";
            case "DISTANCE_ANOMALY": return "距离异常";
            case "EFFICIENCY_DROP": return "效率下降";
            case "ROUTE_ANOMALY": return "路径异常";
            case "LOAD_ANOMALY": return "负载异常";
            default: return anomalyType;
        }
    }

    public BigDecimal getDeviationPercentage() {
        if (anomalyValue != null && thresholdValue != null && thresholdValue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal deviation = anomalyValue.subtract(thresholdValue);
            return deviation.divide(thresholdValue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public String getUrgencyLevel() {
        if ("HIGH".equalsIgnoreCase(anomalySeverity)) {
            return "紧急处理";
        } else if ("MEDIUM".equalsIgnoreCase(anomalySeverity)) {
            return "优先处理";
        } else if ("LOW".equalsIgnoreCase(anomalySeverity)) {
            return "常规处理";
        }
        return "待评估";
    }

    public String getResolutionStatus() {
        if (isResolved != null && isResolved) {
            return "已解决";
        }
        return "待处理";
    }
}