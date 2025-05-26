package com.logistics.service.service;

import com.logistics.service.dao.entity.SpatialAnalysisMetrics;
import com.logistics.service.dao.mapper.SpatialAnalysisMetricsMapper;
import com.logistics.service.dto.SpatialAnalysisDTO;
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
public class SpatialAnalysisService {

    @Autowired
    private SpatialAnalysisMetricsMapper spatialAnalysisMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取指定城市的空间分析数据
     */
    public List<SpatialAnalysisDTO> getSpatialAnalysisByCity(String city, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("spatial_analysis:%s:%s:%s", city, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<SpatialAnalysisDTO> cache = (List<SpatialAnalysisDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取空间分析[city={}], size={}", city, cache.size());
                return cache;
            }

            log.info("🔍 Redis 未命中，查询 MySQL 空间分析[city={}]", city);
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findByCityAndDateRange(city, startDate, endDate);

            List<SpatialAnalysisDTO> result = metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 60, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存空间分析[city={}]，ttl=60m", city);
            }

            return result;
        } catch (Exception e) {
            log.error("获取空间分析数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 按AOI类型获取空间分析
     */
    public List<SpatialAnalysisDTO> getSpatialAnalysisByAoiType(String city, Integer aoiType, LocalDate date) {
        try {
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findByCityAndAoiTypeAndDate(city, aoiType, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按AOI类型获取空间分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取热点区域分析
     */
    public List<SpatialAnalysisDTO> getHotspotAnalysis(String city, LocalDate date) {
        try {
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findHotspotsByCity(city, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取热点区域分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取密度分析数据
     */
    public List<SpatialAnalysisDTO> getDensityAnalysis(String city, LocalDate date) {
        try {
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findDensityAnalysisByCity(city, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取密度分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日空间分析
     */
    public List<SpatialAnalysisDTO> getTodaySpatialAnalysis(String city) {
        LocalDate today = LocalDate.now();
        return getSpatialAnalysisByCity(city, today, today);
    }

    /**
     * 按AOI ID获取详细分析
     */
    public List<SpatialAnalysisDTO> getAnalysisByAoiId(String aoiId, LocalDate startDate, LocalDate endDate) {
        try {
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findByAoiIdAndDateRange(aoiId, startDate, endDate);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按AOI ID获取分析失败", e);
            return new ArrayList<>();
        }
    }

    private SpatialAnalysisDTO convertToDTO(SpatialAnalysisMetrics metrics) {
        SpatialAnalysisDTO dto = new SpatialAnalysisDTO();
        dto.setCity(metrics.getCity());
        dto.setAnalysisDate(metrics.getAnalysisDate());
        dto.setAnalysisHour(metrics.getAnalysisHour());
        dto.setLngGrid(metrics.getLngGrid());
        dto.setLatGrid(metrics.getLatGrid());
        dto.setAoiId(metrics.getAoiId());
        dto.setAoiType(metrics.getAoiType());
        dto.setDeliveryCount(metrics.getDeliveryCount());
        dto.setPickupCount(metrics.getPickupCount());
        dto.setUniqueCouriers(metrics.getUniqueCouriers());
        dto.setOrdersInAoi(metrics.getOrdersInAoi());
        dto.setCouriersInAoi(metrics.getCouriersInAoi());
        dto.setAvgDeliveryTime(metrics.getAvgDeliveryTime() != null ? BigDecimal.valueOf(metrics.getAvgDeliveryTime()) : null);
        dto.setAvgPickupTime(metrics.getAvgPickupTime() != null ? BigDecimal.valueOf(metrics.getAvgPickupTime()) : null);
        dto.setAvgDeliveryDistance(metrics.getAvgDeliveryDistance() != null ? BigDecimal.valueOf(metrics.getAvgDeliveryDistance()) : null);
        dto.setAvgPickupDistance(metrics.getAvgPickupDistance() != null ? BigDecimal.valueOf(metrics.getAvgPickupDistance()) : null);
        dto.setDeliveryDensity(metrics.getDeliveryDensity() != null ? BigDecimal.valueOf(metrics.getDeliveryDensity()) : null);
        dto.setPickupDensity(metrics.getPickupDensity() != null ? BigDecimal.valueOf(metrics.getPickupDensity()) : null);
        dto.setOrdersPerCourier(metrics.getOrdersPerCourier() != null ? BigDecimal.valueOf(metrics.getOrdersPerCourier()) : null);
        return dto;
    }
}