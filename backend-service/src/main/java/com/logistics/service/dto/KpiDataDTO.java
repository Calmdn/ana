package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class KpiDataDTO {
    private String city;
    private LocalDate date;
    private Integer hour;
    private Long totalOrders;
    private Integer activeCouriers;
    private Integer coverageAois;
    private BigDecimal ordersPerCourier;
    private BigDecimal ordersPerAoi;
    private BigDecimal efficiencyScore;

    @Override
    public String toString() {
        return String.format("KpiDataDTO{city='%s', date=%s, hour=%d, totalOrders=%d}",
                city, date, hour, totalOrders);
    }
}