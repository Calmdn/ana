package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OperationalEfficiencyMetrics {
    private String city;
    private Integer regionId;
    private Integer courierId;
    private LocalDate date;
    private Long totalOrders;
    private Long uniqueAoiServed;
    private Double totalDistance;
    private Double totalWorkingHours;
    private Double avgDeliveryTime;
    private Double ordersPerHour;
    private Double distancePerOrder;
    private Double efficiencyScore;
}