package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OperationalEfficiencyDTO {
    private String city;
    private Integer regionId;
    private Integer courierId;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private Long totalOrders;
    private Long uniqueAoiServed;
    private BigDecimal totalDistance;
    private BigDecimal totalWorkingHours;
    private BigDecimal avgDeliveryTime;
    private BigDecimal ordersPerHour;
    private BigDecimal distancePerOrder;
    private BigDecimal efficiencyScore;

    // 计算属性
    public BigDecimal getAoiCoverageRate() {
        if (totalOrders != null && totalOrders > 0 && uniqueAoiServed != null) {
            return BigDecimal.valueOf(uniqueAoiServed).divide(BigDecimal.valueOf(totalOrders), 4, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getDistanceEfficiency() {
        if (totalDistance != null && totalDistance.compareTo(BigDecimal.ZERO) > 0 && totalOrders != null && totalOrders > 0) {
            return BigDecimal.valueOf(totalOrders).divide(totalDistance, 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getEfficiencyLevel() {
        if (efficiencyScore != null) {
            if (efficiencyScore.compareTo(BigDecimal.valueOf(0.9)) >= 0) return "优秀";
            if (efficiencyScore.compareTo(BigDecimal.valueOf(0.8)) >= 0) return "良好";
            if (efficiencyScore.compareTo(BigDecimal.valueOf(0.7)) >= 0) return "一般";
            if (efficiencyScore.compareTo(BigDecimal.valueOf(0.6)) >= 0) return "偏低";
            return "需改进";
        }
        return "未评估";
    }

    public String getWorkloadLevel() {
        if (ordersPerHour != null) {
            if (ordersPerHour.compareTo(BigDecimal.valueOf(8)) >= 0) return "高强度";
            if (ordersPerHour.compareTo(BigDecimal.valueOf(5)) >= 0) return "中等";
            if (ordersPerHour.compareTo(BigDecimal.valueOf(2)) >= 0) return "轻松";
            return "空闲";
        }
        return "未知";
    }
}