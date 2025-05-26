package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ComprehensiveReport {
    private Long id;
    private String city;
    private String regionId;
    private LocalDate analysisDate;
    private LocalDate weekStart;
    private String reportType; // daily, weekly
    private Integer totalDeliveries;
    private Integer totalPickups;
    private Integer activeCouriers;
    private Integer activePickupCouriers;
    private Integer servedAois;
    private Double avgDeliveryTime;
    private Double avgPickupTime;
    private Double totalDistance;
    private Integer fastDeliveries;
    private Integer onTimePickups;
    private Double avgOrdersPerCourier;
    private Double avgDistancePerOrder;
    private Double fastDeliveryRate;
    private Double onTimePickupRate;
    private LocalDateTime generatedAt;
    private LocalDateTime createdAt;
}