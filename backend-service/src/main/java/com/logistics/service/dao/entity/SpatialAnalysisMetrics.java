package com.logistics.service.dao.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SpatialAnalysisMetrics {
    private Long id;
    private String city;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private BigDecimal lngGrid;
    private BigDecimal latGrid;
    private String aoiId;
    private Integer aoiType;
    private Integer deliveryCount;
    private Integer pickupCount;
    private Integer uniqueCouriers;
    private Integer ordersInAoi;
    private Integer couriersInAoi;
    private Double avgDeliveryTime;
    private Double avgPickupTime;
    private Double avgDeliveryDistance;
    private Double avgPickupDistance;
    private Double deliveryDensity;
    private Double pickupDensity;
    private Double ordersPerCourier;
    private LocalDateTime createdAt;
}