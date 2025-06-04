package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OperationalEfficiencyDTO {
    private String city;
    private Integer regionId;
    private Integer courierId;
    private LocalDate date;
    private Long totalOrders;
    private Long uniqueAoiServed;
    private BigDecimal totalDistance;
    private BigDecimal totalWorkingHours;
    private BigDecimal avgDeliveryTime;
    private BigDecimal ordersPerHour;
    private BigDecimal distancePerOrder;
    private BigDecimal efficiencyScore;


    // 便于调试和日志输出
    @Override
    public String toString() {
        return String.format("OperationalEfficiencyDTO{city='%s', regionId=%d, courierId=%d, date=%s, " +
                        "totalOrders=%d, efficiencyScore=%s, ordersPerHour=%s, avgDeliveryTime=%s}",
                city, regionId, courierId, date, totalOrders, efficiencyScore, ordersPerHour, avgDeliveryTime);
    }
}