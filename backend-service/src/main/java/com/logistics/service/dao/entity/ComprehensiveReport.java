package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ComprehensiveReport {
    private String city;
    private Integer regionId;
    private LocalDate date;
    private String reportType; // daily, weekly
    private Long totalDeliveries;
    private Long activeCouriers;
    private Long servedAois;
    private Double avgDeliveryTime;
    private Double totalDistance;
    private Long fastDeliveries;
    private Double avgOrdersPerCourier;
    private Double avgDistancePerOrder;
    private Double fastDeliveryRate;
    private LocalDateTime generatedAt;
}