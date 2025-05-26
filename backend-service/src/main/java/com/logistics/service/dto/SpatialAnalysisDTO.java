package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SpatialAnalysisDTO {
    private String city;
    private LocalDate analysisDate;
    private Integer analysisHour;
    private BigDecimal lngGrid;
    private BigDecimal latGrid;
    private String aoiId;
    private Integer aoiType;
    private Integer deliveryCount;
    private Integer pickupCount;
    private Integer uniqueCouriers;
    private Integer ordersInAoi;
    private Integer couriersInAoi;
    private BigDecimal avgDeliveryTime;
    private BigDecimal avgPickupTime;
    private BigDecimal avgDeliveryDistance;
    private BigDecimal avgPickupDistance;
    private BigDecimal deliveryDensity;
    private BigDecimal pickupDensity;
    private BigDecimal ordersPerCourier;

    // 计算属性
    public Integer getTotalOrders() {
        int delivery = deliveryCount != null ? deliveryCount : 0;
        int pickup = pickupCount != null ? pickupCount : 0;
        return delivery + pickup;
    }

    public BigDecimal getOverallDensity() {
        if (deliveryDensity != null && pickupDensity != null) {
            return deliveryDensity.add(pickupDensity);
        }
        return deliveryDensity != null ? deliveryDensity : (pickupDensity != null ? pickupDensity : BigDecimal.ZERO);
    }

    public String getActivityLevel() {
        Integer total = getTotalOrders();
        if (total >= 100) return "超高活跃";
        if (total >= 50) return "高活跃";
        if (total >= 20) return "中等活跃";
        if (total >= 5) return "低活跃";
        return "极低活跃";
    }

    public String getAoiTypeDescription() {
        if (aoiType != null) {
            switch (aoiType) {
                case 1: return "商业区";
                case 2: return "住宅区";
                case 3: return "工业区";
                case 4: return "商务区";
                case 5: return "混合区";
                default: return "其他区域";
            }
        }
        return "未知";
    }

    public BigDecimal getCourierEfficiency() {
        if (uniqueCouriers != null && uniqueCouriers > 0) {
            return BigDecimal.valueOf(getTotalOrders()).divide(BigDecimal.valueOf(uniqueCouriers), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getDensityLevel() {
        BigDecimal density = getOverallDensity();
        if (density.compareTo(BigDecimal.valueOf(10)) >= 0) return "高密度";
        if (density.compareTo(BigDecimal.valueOf(5)) >= 0) return "中密度";
        if (density.compareTo(BigDecimal.valueOf(1)) >= 0) return "低密度";
        return "极低密度";
    }
}