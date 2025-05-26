package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OperationalEfficiencyMetrics {
    private Long id;
    private String city;
    private Integer regionId;
    private Integer courierId;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private Long totalOrders;
    private Long uniqueAoiServed;
    private Double totalDistance;
    private Double totalWorkingHours;
    private Double avgDeliveryTime;
    private Double ordersPerHour;
    private Double distancePerOrder;
    private Double efficiencyScore;
    private LocalDateTime createdAt;
}