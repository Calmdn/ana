package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PredictiveAnalysisData {
    private Long id;
    private String city;
    private String regionId;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private Integer orderVolume;
    private Integer courierCount;
    private Double avgDuration;
    private Double totalDistance;
    private Integer volumeTrend;
    private Double efficiencyScore;
    private Integer dailyOrders;
    private Integer requiredCouriers;
    private Integer peakHour;
    private Double ordersPerCourierDay;
    private String capacityUtilization;
    private String dataType;
    private LocalDateTime createdAt;
}