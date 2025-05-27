package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TimeEfficiencyMetrics {
    private String city;
    private LocalDate date;
    private Integer hour;
    private Long totalDeliveries;
    private Double avgDeliveryTime;
    private Double medianDeliveryTime;
    private Double p95DeliveryTime;
    private Long fastDeliveries;
    private Long normalDeliveries;
    private Long slowDeliveries;
    private Double fastDeliveryRate;
    private Double slowDeliveryRate;
    private Long totalPickups;
    private Double avgPickupTime;
    private Double medianPickupTime;
    private Double p95PickupTime;
    private Long fastPickups;
    private Long normalPickups;
    private Long slowPickups;
    private Long onTimePickups;
    private Double fastPickupRate;
    private Double slowPickupRate;
    private Double onTimePickupRate;
}