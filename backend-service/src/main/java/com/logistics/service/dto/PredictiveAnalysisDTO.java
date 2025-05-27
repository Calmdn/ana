package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PredictiveAnalysisDTO {
    private String city;
    private String regionId;  // 注意：数据库中是text类型
    private LocalDate dsDate;  // 对应数据库的ds_date字段
    private Integer hour;
    private Long orderVolume;  // 数据库中是bigint
    private Long courierCount; // 数据库中是bigint
    private BigDecimal avgDuration;
    private BigDecimal totalDistance;
    private Long volumeTrend;  // 数据库中是bigint
    private BigDecimal efficiencyScore;
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

    public BigDecimal getOrdersPerCourier() {
        if (courierCount != null && courierCount > 0 && orderVolume != null) {
            return BigDecimal.valueOf(orderVolume)
                    .divide(BigDecimal.valueOf(courierCount), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getWorkloadLevel() {
        BigDecimal ordersPerCourier = getOrdersPerCourier();
        if (ordersPerCourier.compareTo(BigDecimal.valueOf(50)) >= 0) return "高负荷";
        if (ordersPerCourier.compareTo(BigDecimal.valueOf(30)) >= 0) return "中等负荷";
        if (ordersPerCourier.compareTo(BigDecimal.valueOf(10)) >= 0) return "轻负荷";
        return "空闲";
    }

    public String getTimeSlotDescription() {
        if (hour != null) {
            if (hour >= 6 && hour < 12) return "上午时段";
            if (hour >= 12 && hour < 18) return "下午时段";
            if (hour >= 18 && hour < 22) return "晚间时段";
            if (hour >= 22 || hour < 6) return "深夜时段";
        }
        return "未知时段";
    }

    public String getEfficiencyLevel() {
        if (efficiencyScore != null) {
            if (efficiencyScore.compareTo(BigDecimal.valueOf(90)) >= 0) return "优秀";
            if (efficiencyScore.compareTo(BigDecimal.valueOf(80)) >= 0) return "良好";
            if (efficiencyScore.compareTo(BigDecimal.valueOf(70)) >= 0) return "一般";
            if (efficiencyScore.compareTo(BigDecimal.valueOf(60)) >= 0) return "偏低";
            return "需改进";
        }
        return "未评估";
    }

    @Override
    public String toString() {
        return String.format("PredictiveAnalysisDTO{city='%s', regionId='%s', dsDate=%s, hour=%d, " +
                        "orderVolume=%d, courierCount=%d, dataType='%s'}",
                city, regionId, dsDate, hour, orderVolume, courierCount, dataType);
    }
}