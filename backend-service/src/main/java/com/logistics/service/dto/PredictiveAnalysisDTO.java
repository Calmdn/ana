package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PredictiveAnalysisDTO {
    private String city;
    private String regionId;
    private LocalDate dsDate;
    private Integer hour;
    private Long orderVolume;
    private Long courierCount;
    private BigDecimal avgDuration;
    private BigDecimal totalDistance;
    private Long volumeTrend;
    private BigDecimal efficiencyScore;
    private String dataType;


    @Override
    public String toString() {
        return String.format("PredictiveAnalysisDTO{city='%s', regionId='%s', dsDate=%s, hour=%d, " +
                        "orderVolume=%d, courierCount=%d, dataType='%s'}",
                city, regionId, dsDate, hour, orderVolume, courierCount, dataType);
    }
}