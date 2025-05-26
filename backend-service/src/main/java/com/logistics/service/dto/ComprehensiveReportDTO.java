package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ComprehensiveReportDTO {
    private String city;
    private String regionId;
    private LocalDate analysisDate;
    private LocalDate weekStart;
    private String reportType;
    private Integer totalDeliveries;
    private Integer totalPickups;
    private Integer activeCouriers;
    private Integer activePickupCouriers;
    private Integer servedAois;
    private BigDecimal avgDeliveryTime;
    private BigDecimal avgPickupTime;
    private BigDecimal totalDistance;
    private Integer fastDeliveries;
    private Integer onTimePickups;
    private BigDecimal avgOrdersPerCourier;
    private BigDecimal avgDistancePerOrder;
    private BigDecimal fastDeliveryRate;
    private BigDecimal onTimePickupRate;

    // 计算属性
    public BigDecimal getOverallEfficiencyScore() {
        if (fastDeliveryRate != null && onTimePickupRate != null) {
            return fastDeliveryRate.add(onTimePickupRate).divide(BigDecimal.valueOf(2), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getAoiCoverage() {
        if (activeCouriers != null && activeCouriers > 0 && servedAois != null) {
            return BigDecimal.valueOf(servedAois).divide(BigDecimal.valueOf(activeCouriers), 2, BigDecimal.ROUND_HALF_UP);
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
}