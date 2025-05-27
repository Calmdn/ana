package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PredictiveAnalysisData {
    private String city;
    private LocalDate dsDate;
    private Integer hour;
    private Long orderVolume;
    private Long courierCount;
    private Double avgDuration;
    private Double totalDistance;
    private Long volumeTrend;
    private Double efficiencyScore;
    private String dataType;
    private String regionId;
}