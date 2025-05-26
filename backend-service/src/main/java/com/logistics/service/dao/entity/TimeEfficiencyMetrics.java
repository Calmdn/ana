package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TimeEfficiencyMetrics {
    private Long id;
    private String city;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private Integer totalDeliveries;
    private Double avgDeliveryTime;
    private Double medianDeliveryTime;
    private Double p95DeliveryTime;
    private Integer fastDeliveries;
    private Integer normalDeliveries;
    private Integer slowDeliveries;
    private Double fastDeliveryRate;
    private Double slowDeliveryRate;
    private Integer totalPickups;
    private Double avgPickupTime;
    private Double medianPickupTime;
    private Double p95PickupTime;
    private Integer fastPickups;
    private Integer normalPickups;
    private Integer slowPickups;
    private Double fastPickupRate;
    private Double slowPickupRate;
    private Integer onTimePickups;
    private Double onTimePickupRate;
    private LocalDateTime createdAt;
}