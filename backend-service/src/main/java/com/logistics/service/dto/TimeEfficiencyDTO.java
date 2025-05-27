package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TimeEfficiencyDTO {
    private String city;
    private LocalDate date;  // 对应数据库的date字段
    private Long totalDeliveries;
    private BigDecimal avgDeliveryTime;
    private Long fastDeliveries;
    private Long slowDeliveries;
    private BigDecimal fastDeliveryRate;
    private BigDecimal slowDeliveryRate;

    // 计算属性
    public Long getNormalDeliveries() {
        if (totalDeliveries != null && fastDeliveries != null && slowDeliveries != null) {
            return totalDeliveries - fastDeliveries - slowDeliveries;
        }
        return 0L;
    }

    public BigDecimal getNormalDeliveryRate() {
        if (totalDeliveries != null && totalDeliveries > 0) {
            long normal = getNormalDeliveries();
            return BigDecimal.valueOf(normal)
                    .divide(BigDecimal.valueOf(totalDeliveries), 4, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getDeliveryPerformanceLevel() {
        if (fastDeliveryRate != null) {
            if (fastDeliveryRate.compareTo(BigDecimal.valueOf(0.8)) >= 0) return "优秀";
            if (fastDeliveryRate.compareTo(BigDecimal.valueOf(0.6)) >= 0) return "良好";
            if (fastDeliveryRate.compareTo(BigDecimal.valueOf(0.4)) >= 0) return "一般";
            if (fastDeliveryRate.compareTo(BigDecimal.valueOf(0.2)) >= 0) return "偏低";
            return "需改进";
        }
        return "未评估";
    }

    public String getEfficiencyLevel() {
        if (slowDeliveryRate != null) {
            if (slowDeliveryRate.compareTo(BigDecimal.valueOf(0.1)) <= 0) return "高效";
            if (slowDeliveryRate.compareTo(BigDecimal.valueOf(0.2)) <= 0) return "良好";
            if (slowDeliveryRate.compareTo(BigDecimal.valueOf(0.3)) <= 0) return "一般";
            return "需改进";
        }
        return "未知";
    }

    public String getTimePerformanceDescription() {
        if (avgDeliveryTime != null) {
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(30)) <= 0) return "极快";
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(45)) <= 0) return "快速";
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(60)) <= 0) return "正常";
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(90)) <= 0) return "较慢";
            return "慢";
        }
        return "未知";
    }

    public BigDecimal getOverallEfficiencyScore() {
        if (fastDeliveryRate != null && slowDeliveryRate != null) {
            // 快速配送率占60%，慢速配送率（倒数）占40%
            BigDecimal fastScore = fastDeliveryRate.multiply(BigDecimal.valueOf(0.6));
            BigDecimal slowScore = BigDecimal.ONE.subtract(slowDeliveryRate).multiply(BigDecimal.valueOf(0.4));
            return fastScore.add(slowScore).multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public String getWorkloadLevel() {
        if (totalDeliveries != null) {
            if (totalDeliveries >= 1000) return "超高负荷";
            if (totalDeliveries >= 500) return "高负荷";
            if (totalDeliveries >= 200) return "中等负荷";
            if (totalDeliveries >= 50) return "轻负荷";
            return "空闲";
        }
        return "未知";
    }

    @Override
    public String toString() {
        return String.format("TimeEfficiencyDTO{city='%s', date=%s, totalDeliveries=%d, " +
                        "avgDeliveryTime=%s, fastDeliveryRate=%s, slowDeliveryRate=%s}",
                city, date, totalDeliveries, avgDeliveryTime, fastDeliveryRate, slowDeliveryRate);
    }
}