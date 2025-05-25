package com.logistics.service.dao.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RealtimeKpi {
    private Long id;
    private String city;
    private LocalDate date;
    private Integer hour;
    private Long totalOrders;
    private Integer activeCouriers;
    private Integer coverageAois;
    private BigDecimal ordersPerCourier;
    private BigDecimal ordersPerAoi;
    private BigDecimal efficiencyScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}