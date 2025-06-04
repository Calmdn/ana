package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SpatialAnalysisDTO {
    private String city;
    private LocalDate date;
    private BigDecimal lngGrid;
    private BigDecimal latGrid;
    private Long deliveryCount;
    private Long uniqueCouriers;
    private BigDecimal avgDeliveryTime;
    private BigDecimal avgDeliveryDistance;
    private BigDecimal deliveryDensity;


    @Override
    public String toString() {
        return String.format("SpatialAnalysisDTO{city='%s', date=%s, grid=(%.4f,%.4f), " +
                        "deliveryCount=%d, uniqueCouriers=%d, deliveryDensity=%s}",
                city, date,
                lngGrid != null ? lngGrid.doubleValue() : 0.0,
                latGrid != null ? latGrid.doubleValue() : 0.0,
                deliveryCount, uniqueCouriers, deliveryDensity);
    }
}