package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PredictiveAnalysisDTO {
    private String city;
    private String regionId;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private Integer orderVolume;
    private Integer courierCount;
    private BigDecimal avgDuration;
    private BigDecimal totalDistance;
    private Integer volumeTrend;
    private BigDecimal efficiencyScore;
    private Integer dailyOrders;
    private Integer requiredCouriers;
    private Integer peakHour;
    private BigDecimal ordersPerCourierDay;
    private String capacityUtilization;
    private String dataType;

    // 计算属性
    public String getTrendDirection() {
        if (volumeTrend != null) {
            if (volumeTrend > 10) return "强烈上升";
            if (volumeTrend > 5) return "上升";
            if (volumeTrend > -5) return "平稳";
            if (volumeTrend > -10) return "下降";
            return "强烈下降";
        }
        return "未知";
    }

    public BigDecimal getCourierUtilizationRate() {
        if (courierCount != null && courierCount > 0 && requiredCouriers != null) {
            return BigDecimal.valueOf(courierCount).divide(BigDecimal.valueOf(requiredCouriers), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getCapacityStatus() {
        BigDecimal utilization = getCourierUtilizationRate();
        if (utilization.compareTo(BigDecimal.valueOf(1.2)) >= 0) return "产能过剩";
        if (utilization.compareTo(BigDecimal.valueOf(0.9)) >= 0) return "产能充足";
        if (utilization.compareTo(BigDecimal.valueOf(0.8)) >= 0) return "产能适中";
        if (utilization.compareTo(BigDecimal.valueOf(0.7)) >= 0) return "产能紧张";
        return "产能不足";
    }

    public String getPeakHourDescription() {
        if (peakHour != null) {
            if (peakHour >= 6 && peakHour < 12) return "上午高峰";
            if (peakHour >= 12 && peakHour < 18) return "下午高峰";
            if (peakHour >= 18 && peakHour < 22) return "晚间高峰";
            if (peakHour >= 22 || peakHour < 6) return "深夜时段";
        }
        return "未知时段";
    }
}