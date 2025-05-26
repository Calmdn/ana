package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CostAnalysisMetrics {
    private Long id;
    private String city;
    private String regionId;
    private String courierId;
    private LocalDate analysisDate;
    private Double totalCost;
    private Double totalFuelCost;
    private Double totalTimeCost;
    private Integer totalOrders;
    private Double totalDistance;
    private Double costPerOrder;
    private Double costPerKm;
    private Double fuelCostRatio;
    private Double workingHours;
    private Double productivityScore;
    private String efficiencyRating;
    private String analysisType;
    private LocalDateTime createdAt;
}