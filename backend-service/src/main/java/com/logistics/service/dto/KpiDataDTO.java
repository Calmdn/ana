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
    private BigDecimal efficiencyScore;
    private BigDecimal fastDeliveryRate;

    // 计算属性
    public BigDecimal getOrdersPerCourier() {
        if (activeCouriers != null && activeCouriers > 0) {
            return BigDecimal.valueOf(totalOrders).divide(BigDecimal.valueOf(activeCouriers), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }
}