package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CostAnalysisDTO {
    private String city;
    private String regionId;
    private String courierId;
    private LocalDate analysisDate;
    private BigDecimal totalCost;
    private BigDecimal totalFuelCost;
    private BigDecimal totalTimeCost;
    private Integer totalOrders;
    private BigDecimal totalDistance;
    private BigDecimal costPerOrder;
    private BigDecimal costPerKm;
    private BigDecimal fuelCostRatio;
    private BigDecimal workingHours;
    private BigDecimal productivityScore;
    private String efficiencyRating;
    private String analysisType;

    // 计算属性
    public BigDecimal getHourlyCost() {
        if (workingHours != null && workingHours.compareTo(BigDecimal.ZERO) > 0 && totalCost != null) {
            return totalCost.divide(workingHours, 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getOrdersPerHour() {
        if (workingHours != null && workingHours.compareTo(BigDecimal.ZERO) > 0 && totalOrders != null) {
            return BigDecimal.valueOf(totalOrders).divide(workingHours, 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getCostEfficiencyLevel() {
        if (costPerOrder != null) {
            if (costPerOrder.compareTo(BigDecimal.valueOf(10)) <= 0) return "优秀";
            if (costPerOrder.compareTo(BigDecimal.valueOf(20)) <= 0) return "良好";
            if (costPerOrder.compareTo(BigDecimal.valueOf(30)) <= 0) return "一般";
            return "需优化";
        }
        return "未知";
    }
}