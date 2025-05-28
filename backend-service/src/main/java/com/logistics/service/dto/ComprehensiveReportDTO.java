package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ComprehensiveReportDTO {
    private String city;
    private Integer regionId;
    private LocalDate date;
    private Long totalDeliveries;
    private Long activeCouriers;
    private Long servedAois;
    private Double avgDeliveryTime;
    private Double totalDistance;
    private Long fastDeliveries;
    private Double avgOrdersPerCourier;
    private Double avgDistancePerOrder;
    private Double fastDeliveryRate;
    private String reportType;
    private LocalDateTime generatedAt;

}