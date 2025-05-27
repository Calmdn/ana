package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SpatialAnalysisDTO {
    private String city;
    private LocalDate date;  // 对应数据库的date字段
    private BigDecimal lngGrid;
    private BigDecimal latGrid;
    private Long deliveryCount;
    private Long uniqueCouriers;
    private BigDecimal avgDeliveryTime;
    private BigDecimal avgDeliveryDistance;
    private BigDecimal deliveryDensity;

    // 计算属性
    public String getActivityLevel() {
        if (deliveryCount != null) {
            if (deliveryCount >= 100) return "超高活跃";
            if (deliveryCount >= 50) return "高活跃";
            if (deliveryCount >= 20) return "中等活跃";
            if (deliveryCount >= 5) return "低活跃";
        }
        return "极低活跃";
    }

    public BigDecimal getCourierEfficiency() {
        if (uniqueCouriers != null && uniqueCouriers > 0 && deliveryCount != null) {
            return BigDecimal.valueOf(deliveryCount)
                    .divide(BigDecimal.valueOf(uniqueCouriers), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getDensityLevel() {
        if (deliveryDensity != null) {
            if (deliveryDensity.compareTo(BigDecimal.valueOf(10)) >= 0) return "高密度";
            if (deliveryDensity.compareTo(BigDecimal.valueOf(5)) >= 0) return "中密度";
            if (deliveryDensity.compareTo(BigDecimal.valueOf(1)) >= 0) return "低密度";
        }
        return "极低密度";
    }

    public String getDeliveryTimeLevel() {
        if (avgDeliveryTime != null) {
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(30)) <= 0) return "极快";
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(45)) <= 0) return "快速";
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(60)) <= 0) return "正常";
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(90)) <= 0) return "较慢";
            return "慢";
        }
        return "未知";
    }

    public String getDistanceLevel() {
        if (avgDeliveryDistance != null) {
            if (avgDeliveryDistance.compareTo(BigDecimal.valueOf(2)) <= 0) return "短距离";
            if (avgDeliveryDistance.compareTo(BigDecimal.valueOf(5)) <= 0) return "中距离";
            if (avgDeliveryDistance.compareTo(BigDecimal.valueOf(10)) <= 0) return "长距离";
            return "超长距离";
        }
        return "未知";
    }

    public String getGridZone() {
        if (lngGrid != null && latGrid != null) {
            return String.format("Grid(%.4f,%.4f)", lngGrid.doubleValue(), latGrid.doubleValue());
        }
        return "未知网格";
    }

    @Override
    public String toString() {
        return String.format("SpatialAnalysisDTO{city='%s', date=%s, grid=(%.4f,%.4f), " +
                        "deliveryCount=%d, uniqueCouriers=%d, deliveryDensity=%s}",
                city, date,
                lngGrid != null ? lngGrid.doubleValue() : 0.0,
                latGrid != null ? latGrid.doubleValue() : 0.0,
                deliveryCount, uniqueCouriers, deliveryDensity);
    }
}