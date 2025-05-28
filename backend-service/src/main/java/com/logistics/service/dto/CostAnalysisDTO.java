package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CostAnalysisDTO {
    private String city;
    private String regionId;
    private String courierId;
    private LocalDate analysisDate;
    private BigDecimal totalCost;
    private BigDecimal totalFuelCost;
    private BigDecimal totalTimeCost;
    private Integer totalOrders;
    private BigDecimal totalDistance;
    private BigDecimal costPerOrder;
    private BigDecimal costPerKm;
    private BigDecimal fuelCostRatio;
    private BigDecimal workingHours;
    private BigDecimal productivityScore;
    private String efficiencyRating;
    private String analysisType;

}