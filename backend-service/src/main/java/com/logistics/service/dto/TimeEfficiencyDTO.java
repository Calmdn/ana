package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TimeEfficiencyDTO {
    private String city;
    private LocalDate date;
    private Long totalDeliveries;
    private BigDecimal avgDeliveryTime;
    private Long fastDeliveries;
    private Long slowDeliveries;
    private BigDecimal fastDeliveryRate;
    private BigDecimal slowDeliveryRate;


    @Override
    public String toString() {
        return String.format("TimeEfficiencyDTO{city='%s', date=%s, totalDeliveries=%d, " +
                        "avgDeliveryTime=%s, fastDeliveryRate=%s, slowDeliveryRate=%s}",
                city, date, totalDeliveries, avgDeliveryTime, fastDeliveryRate, slowDeliveryRate);
    }
}