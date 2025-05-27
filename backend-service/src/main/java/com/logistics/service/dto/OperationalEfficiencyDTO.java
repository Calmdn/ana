package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OperationalEfficiencyDTO {
    private String city;
    private Integer regionId;
    private Integer courierId;
    private LocalDate date;  // 改为date，与数据库字段一致
    private Long totalOrders;
    private Long uniqueAoiServed;
    private BigDecimal totalDistance;
    private BigDecimal totalWorkingHours;
    private BigDecimal avgDeliveryTime;
    private BigDecimal ordersPerHour;
    private BigDecimal distancePerOrder;
    private BigDecimal efficiencyScore;

    // 新增字段：便于前端展示和分析
    private String efficiencyLevel;     // 效率等级
    private String workloadLevel;       // 工作强度等级
    private BigDecimal aoiCoverageRate; // AOI覆盖率
    private BigDecimal distanceEfficiency; // 距离效率

    // 计算属性：AOI覆盖率
    public BigDecimal getAoiCoverageRate() {
        if (totalOrders != null && totalOrders > 0 && uniqueAoiServed != null) {
            return BigDecimal.valueOf(uniqueAoiServed)
                    .divide(BigDecimal.valueOf(totalOrders), 4, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    // 计算属性：距离效率（订单数/总距离）
    public BigDecimal getDistanceEfficiency() {
        if (totalDistance != null && totalDistance.compareTo(BigDecimal.ZERO) > 0 &&
                totalOrders != null && totalOrders > 0) {
            return BigDecimal.valueOf(totalOrders)
                    .divide(totalDistance, 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    // 计算属性：效率等级
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

    // 计算属性：工作强度等级
    public String getWorkloadLevel() {
        if (ordersPerHour != null) {
            if (ordersPerHour.compareTo(BigDecimal.valueOf(8)) >= 0) return "高强度";
            if (ordersPerHour.compareTo(BigDecimal.valueOf(5)) >= 0) return "中等";
            if (ordersPerHour.compareTo(BigDecimal.valueOf(2)) >= 0) return "轻松";
            return "空闲";
        }
        return "未知";
    }

    // 计算属性：配送时间等级
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

    // 计算属性：单均距离等级
    public String getDistancePerOrderLevel() {
        if (distancePerOrder != null) {
            if (distancePerOrder.compareTo(BigDecimal.valueOf(2)) <= 0) return "短距离";
            if (distancePerOrder.compareTo(BigDecimal.valueOf(5)) <= 0) return "中距离";
            if (distancePerOrder.compareTo(BigDecimal.valueOf(10)) <= 0) return "长距离";
            return "超长距离";
        }
        return "未知";
    }

    // 计算属性：综合评价
    public String getOverallRating() {
        if (efficiencyScore != null && ordersPerHour != null && avgDeliveryTime != null) {
            int score = 0;

            // 效率分评分
            if (efficiencyScore.compareTo(BigDecimal.valueOf(90)) >= 0) score += 40;
            else if (efficiencyScore.compareTo(BigDecimal.valueOf(80)) >= 0) score += 32;
            else if (efficiencyScore.compareTo(BigDecimal.valueOf(70)) >= 0) score += 24;
            else if (efficiencyScore.compareTo(BigDecimal.valueOf(60)) >= 0) score += 16;
            else score += 8;

            // 订单效率评分
            if (ordersPerHour.compareTo(BigDecimal.valueOf(6)) >= 0) score += 30;
            else if (ordersPerHour.compareTo(BigDecimal.valueOf(4)) >= 0) score += 24;
            else if (ordersPerHour.compareTo(BigDecimal.valueOf(2)) >= 0) score += 18;
            else score += 10;

            // 配送时间评分
            if (avgDeliveryTime.compareTo(BigDecimal.valueOf(30)) <= 0) score += 30;
            else if (avgDeliveryTime.compareTo(BigDecimal.valueOf(45)) <= 0) score += 24;
            else if (avgDeliveryTime.compareTo(BigDecimal.valueOf(60)) <= 0) score += 18;
            else score += 10;

            if (score >= 90) return "卓越表现";
            if (score >= 80) return "优秀表现";
            if (score >= 70) return "良好表现";
            if (score >= 60) return "一般表现";
            return "需要改进";
        }
        return "数据不足";
    }

    // 便于调试和日志输出
    @Override
    public String toString() {
        return String.format("OperationalEfficiencyDTO{city='%s', regionId=%d, courierId=%d, date=%s, " +
                        "totalOrders=%d, efficiencyScore=%s, ordersPerHour=%s, avgDeliveryTime=%s}",
                city, regionId, courierId, date, totalOrders, efficiencyScore, ordersPerHour, avgDeliveryTime);
    }
}