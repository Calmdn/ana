package com.logistics.service.service;

import com.logistics.service.dao.entity.CostAnalysisMetrics;
import com.logistics.service.dao.mapper.CostAnalysisMetricsMapper;
import com.logistics.service.dto.CostAnalysisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Slf4j
@Service
public class CostAnalysisService {

    @Autowired
    private CostAnalysisMetricsMapper costAnalysisMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取指定城市的成本分析数据
     */
    public List<CostAnalysisDTO> getCostAnalysisByCity(String city, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("cost_analysis:%s:%s:%s", city, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<CostAnalysisDTO> cache = (List<CostAnalysisDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取成本分析[city={}], size={}", city, cache.size());
                return cache;
            }

            log.info("🔍 Redis 未命中，查询 MySQL 成本分析[city={}]", city);
            List<CostAnalysisMetrics> metrics = costAnalysisMapper.findByCityAndDateRange(city, startDate, endDate);

            List<CostAnalysisDTO> result = metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 90, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存成本分析[city={}]，ttl=90m", city);
            }

            return result;
        } catch (Exception e) {
            log.error("获取成本分析数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定配送员的成本分析
     */
    public List<CostAnalysisDTO> getCostAnalysisByCourier(String courierId, LocalDate startDate, LocalDate endDate) {
        try {
            List<CostAnalysisMetrics> metrics = costAnalysisMapper.findByCourierAndDateRange(courierId, startDate, endDate);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取配送员成本分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日成本分析
     */
    public List<CostAnalysisDTO> getTodayCostAnalysis(String city) {
        LocalDate today = LocalDate.now();
        return getCostAnalysisByCity(city, today, today);
    }

    private CostAnalysisDTO convertToDTO(CostAnalysisMetrics metrics) {
        CostAnalysisDTO dto = new CostAnalysisDTO();
        dto.setCity(metrics.getCity());
        dto.setRegionId(metrics.getRegionId());
        dto.setCourierId(metrics.getCourierId());
        dto.setAnalysisDate(metrics.getAnalysisDate());
        dto.setTotalCost(metrics.getTotalCost() != null ? BigDecimal.valueOf(metrics.getTotalCost()) : null);
        dto.setTotalFuelCost(metrics.getTotalFuelCost() != null ? BigDecimal.valueOf(metrics.getTotalFuelCost()) : null);
        dto.setTotalTimeCost(metrics.getTotalTimeCost() != null ? BigDecimal.valueOf(metrics.getTotalTimeCost()) : null);
        dto.setTotalOrders(metrics.getTotalOrders());
        dto.setTotalDistance(metrics.getTotalDistance() != null ? BigDecimal.valueOf(metrics.getTotalDistance()) : null);
        dto.setCostPerOrder(metrics.getCostPerOrder() != null ? BigDecimal.valueOf(metrics.getCostPerOrder()) : null);
        dto.setCostPerKm(metrics.getCostPerKm() != null ? BigDecimal.valueOf(metrics.getCostPerKm()) : null);
        dto.setFuelCostRatio(metrics.getFuelCostRatio() != null ? BigDecimal.valueOf(metrics.getFuelCostRatio()) : null);
        dto.setWorkingHours(metrics.getWorkingHours() != null ? BigDecimal.valueOf(metrics.getWorkingHours()) : null);
        dto.setProductivityScore(metrics.getProductivityScore() != null ? BigDecimal.valueOf(metrics.getProductivityScore()) : null);
        dto.setEfficiencyRating(metrics.getEfficiencyRating());
        dto.setAnalysisType(metrics.getAnalysisType());
        return dto;
    }
}