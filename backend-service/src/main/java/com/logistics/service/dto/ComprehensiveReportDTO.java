package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ComprehensiveReportDTO {
    private String city;
    private Integer regionId;
    private LocalDate date;
    private Long totalDeliveries;
    private Long activeCouriers;
    private Long servedAois;
    private Double avgDeliveryTime;
    private Double totalDistance;
    private Long fastDeliveries;
    private Double avgOrdersPerCourier;
    private Double avgDistancePerOrder;
    private Double fastDeliveryRate;
    private String reportType;
    private LocalDateTime generatedAt;

    // 计算属性
    public BigDecimal getOverallEfficiencyScore() {
        if (fastDeliveryRate != null) {
            return BigDecimal.valueOf(fastDeliveryRate);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getAoiCoverage() {
        if (activeCouriers != null && activeCouriers > 0 && servedAois != null) {
            return BigDecimal.valueOf(servedAois.doubleValue() / activeCouriers.doubleValue())
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getPerformanceLevel() {
        BigDecimal score = getOverallEfficiencyScore();
        if (score.compareTo(BigDecimal.valueOf(0.8)) >= 0) return "优秀";
        if (score.compareTo(BigDecimal.valueOf(0.6)) >= 0) return "良好";
        if (score.compareTo(BigDecimal.valueOf(0.4)) >= 0) return "一般";
        return "需改进";
    }

    // 获取配送效率指标
    public BigDecimal getDeliveryEfficiency() {
        if (totalDeliveries != null && activeCouriers != null && activeCouriers > 0) {
            return BigDecimal.valueOf(totalDeliveries.doubleValue() / activeCouriers.doubleValue())
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    // 获取快速配送比例（百分比显示）
    public String getFastDeliveryRatePercentage() {
        if (fastDeliveryRate != null) {
            return String.format("%.1f%%", fastDeliveryRate * 100);
        }
        return "0.0%";
    }
}