package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PredictiveAnalysisDTO {
    private String city;
    private String regionId;  // 注意：数据库中是text类型
    private LocalDate dsDate;  // 对应数据库的ds_date字段
    private Integer hour;
    private Long orderVolume;  // 数据库中是bigint
    private Long courierCount; // 数据库中是bigint
    private BigDecimal avgDuration;
    private BigDecimal totalDistance;
    private Long volumeTrend;  // 数据库中是bigint
    private BigDecimal efficiencyScore;
    private String dataType;


    @Override
    public String toString() {
        return String.format("PredictiveAnalysisDTO{city='%s', regionId='%s', dsDate=%s, hour=%d, " +
                        "orderVolume=%d, courierCount=%d, dataType='%s'}",
                city, regionId, dsDate, hour, orderVolume, courierCount, dataType);
    }
}