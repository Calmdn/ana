package com.logistics.service.service;

import com.logistics.service.dao.entity.RealtimeKpi;
import com.logistics.service.dao.mapper.RealtimeKpiMapper;
import com.logistics.service.dto.KpiDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KpiService {

    @Autowired
    private RealtimeKpiMapper realtimeKpiMapper;

    /**
     * 获取指定城市今天的KPI数据
     */
    public List<KpiDataDTO> getTodayKpiByCity(String city) {
        try {
            log.info("获取城市 {} 今日KPI数据", city);

            LocalDate today = LocalDate.now();
            List<RealtimeKpi> kpiList = realtimeKpiMapper.findBycityAndDate(city, today);

            List<KpiDataDTO> result = new ArrayList<>();
            for (RealtimeKpi kpi : kpiList) {
                KpiDataDTO dto = convertToDTO(kpi);
                result.add(dto);
            }

            log.info("获取到 {} 条KPI记录", result.size());
            return result;

        } catch (Exception e) {
            log.error("获取KPI数据失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 获取系统健康状态（简单版）
     */
    public String getSystemHealth() {
        try {
            LocalDate today = LocalDate.now();

            // 检查今天是否有数据
            List<RealtimeKpi> todayData = realtimeKpiMapper.findBycityAndDate("Shanghai", today);

            if (todayData.isEmpty()) {
                return "⚠️ 今日暂无数据";
            }

            return "✅ 系统运行正常，共有 " + todayData.size() + " 条记录";

        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage());
            return "❌ 系统异常: " + e.getMessage();
        }
    }

    /**
     * 实体转DTO
     */
    private KpiDataDTO convertToDTO(RealtimeKpi kpi) {
        KpiDataDTO dto = new KpiDataDTO();
        dto.setCity(kpi.getCity());
        dto.setDate(kpi.getDate());
        dto.setHour(kpi.getHour());
        dto.setTotalOrders(kpi.getTotalOrders());
        dto.setActiveCouriers(kpi.getActiveCouriers());
        dto.setEfficiencyScore(kpi.getEfficiencyScore());

        // 计算快速配送率（这里简化处理）
        if (kpi.getTotalOrders() != null && kpi.getTotalOrders() > 0) {
            // 假设70%的订单是快速配送
            dto.setFastDeliveryRate(kpi.getEfficiencyScore().multiply(java.math.BigDecimal.valueOf(0.7)));
        }

        return dto;
    }
}