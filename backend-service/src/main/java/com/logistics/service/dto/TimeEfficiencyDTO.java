package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TimeEfficiencyDTO {
    private String city;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private Integer totalDeliveries;
    private BigDecimal avgDeliveryTime;
    private BigDecimal medianDeliveryTime;
    private BigDecimal p95DeliveryTime;
    private Integer fastDeliveries;
    private Integer normalDeliveries;
    private Integer slowDeliveries;
    private BigDecimal fastDeliveryRate;
    private BigDecimal slowDeliveryRate;
    private Integer totalPickups;
    private BigDecimal avgPickupTime;
    private BigDecimal medianPickupTime;
    private BigDecimal p95PickupTime;
    private Integer fastPickups;
    private Integer normalPickups;
    private Integer slowPickups;
    private BigDecimal fastPickupRate;
    private BigDecimal slowPickupRate;
    private Integer onTimePickups;
    private BigDecimal onTimePickupRate;

    // 计算属性
    public Integer getTotalOrders() {
        int deliveries = totalDeliveries != null ? totalDeliveries : 0;
        int pickups = totalPickups != null ? totalPickups : 0;
        return deliveries + pickups;
    }

    public BigDecimal getOverallEfficiencyScore() {
        if (fastDeliveryRate != null && onTimePickupRate != null) {
            return fastDeliveryRate.add(onTimePickupRate).divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        }
        if (fastDeliveryRate != null) return fastDeliveryRate;
        if (onTimePickupRate != null) return onTimePickupRate;
        return BigDecimal.ZERO;
    }

    public String getDeliveryPerformanceLevel() {
        if (fastDeliveryRate != null) {
            if (fastDeliveryRate.compareTo(BigDecimal.valueOf(0.9)) >= 0) return "优秀";
            if (fastDeliveryRate.compareTo(BigDecimal.valueOf(0.8)) >= 0) return "良好";
            if (fastDeliveryRate.compareTo(BigDecimal.valueOf(0.7)) >= 0) return "一般";
            if (fastDeliveryRate.compareTo(BigDecimal.valueOf(0.6)) >= 0) return "偏低";
            return "需改进";
        }
        return "未评估";
    }

    public String getPickupPerformanceLevel() {
        if (onTimePickupRate != null) {
            if (onTimePickupRate.compareTo(BigDecimal.valueOf(0.95)) >= 0) return "优秀";
            if (onTimePickupRate.compareTo(BigDecimal.valueOf(0.85)) >= 0) return "良好";
            if (onTimePickupRate.compareTo(BigDecimal.valueOf(0.75)) >= 0) return "一般";
            if (onTimePickupRate.compareTo(BigDecimal.valueOf(0.65)) >= 0) return "偏低";
            return "需改进";
        }
        return "未评估";
    }

    public String getTimeSlotDescription() {
        if (analysisHour != null) {
            if (analysisHour >= 6 && analysisHour < 9) return "早高峰";
            if (analysisHour >= 9 && analysisHour < 12) return "上午时段";
            if (analysisHour >= 12 && analysisHour < 14) return "午餐时段";
            if (analysisHour >= 14 && analysisHour < 17) return "下午时段";
            if (analysisHour >= 17 && analysisHour < 20) return "晚高峰";
            if (analysisHour >= 20 && analysisHour < 23) return "夜间时段";
            return "深夜时段";
        }
        return "全天";
    }

    public BigDecimal getDeliveryVariability() {
        if (p95DeliveryTime != null && medianDeliveryTime != null && medianDeliveryTime.compareTo(BigDecimal.ZERO) > 0) {
            return p95DeliveryTime.divide(medianDeliveryTime, 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}