package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CostAnalysisMetrics {
    private String city;
    private Integer regionId;
    private LocalDate date;
    private Double totalCost;
    private Double totalFuelCost;
    private Double totalTimeCost;
    private Long totalOrders;
    private Double totalDistance;
    private Double costPerOrder;
    private Double costPerKm;
    private Double fuelCostRatio;
    private String analysisType;
}