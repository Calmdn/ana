package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SpatialAnalysisMetrics {
    private String city;
    private LocalDate date;
    private Double lngGrid;
    private Double latGrid;
    private Long deliveryCount;
    private Long uniqueCouriers;
    private Double avgDeliveryTime;
    private Double avgDeliveryDistance;
    private Double deliveryDensity;
}