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


    // 便于调试和日志输出
    @Override
    public String toString() {
        return String.format("OperationalEfficiencyDTO{city='%s', regionId=%d, courierId=%d, date=%s, " +
                        "totalOrders=%d, efficiencyScore=%s, ordersPerHour=%s, avgDeliveryTime=%s}",
                city, regionId, courierId, date, totalOrders, efficiencyScore, ordersPerHour, avgDeliveryTime);
    }
}