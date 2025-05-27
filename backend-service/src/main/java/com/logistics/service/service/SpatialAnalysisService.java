package com.logistics.service.service;

import com.logistics.service.dao.entity.SpatialAnalysisMetrics;
import com.logistics.service.dao.mapper.SpatialAnalysisMetricsMapper;
import com.logistics.service.dto.SpatialAnalysisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SpatialAnalysisService {

    @Autowired
    private SpatialAnalysisMetricsMapper spatialAnalysisMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存空间分析数据
     */
    @Transactional
    public int saveSpatialAnalysis(SpatialAnalysisMetrics metrics) {
        validateSpatialAnalysisMetrics(metrics);
        return spatialAnalysisMapper.insertSpatialAnalysis(metrics);
    }

    /**
     * 批量保存空间分析数据
     */
    @Transactional
    public int batchSaveSpatialAnalysis(List<SpatialAnalysisMetrics> metricsList) {
        for (SpatialAnalysisMetrics metrics : metricsList) {
            validateSpatialAnalysisMetrics(metrics);
        }
        return spatialAnalysisMapper.batchInsertSpatialAnalysis(metricsList);
    }

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
     * 根据城市和日期获取空间分析
     */
    public List<SpatialAnalysisDTO> getSpatialAnalysisByDate(String city, LocalDate date) {
        try {
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findByCityAndDate(city, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据日期获取空间分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据地理范围获取空间分析
     */
    public List<SpatialAnalysisDTO> getSpatialAnalysisByGeoRange(String city, Double minLng, Double maxLng,
                                                                 Double minLat, Double maxLat,
                                                                 LocalDate startDate, LocalDate endDate) {
        try {
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findByGeoRange(
                    city, minLng, maxLng, minLat, maxLat, startDate, endDate);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据地理范围获取空间分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取热点区域分析
     */
    public List<SpatialAnalysisDTO> getHotspotAnalysis(String city, LocalDate date) {
        return getHotspotAnalysis(city, date, 20);
    }

    /**
     * 获取热点区域分析（指定数量）
     */
    public List<SpatialAnalysisDTO> getHotspotAnalysis(String city, LocalDate date, int limit) {
        try {
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findHotspotsByCity(city, date, limit);
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
        return getSpatialAnalysisByDate(city, today);
    }

    /**
     * 获取配送密度热点
     */
    public List<Map<String, Object>> getDeliveryDensityHotspots(String city, LocalDate startDate, int limit) {
        return spatialAnalysisMapper.getDeliveryDensityHotspots(city, startDate, limit);
    }

    /**
     * 获取配送时间热图数据
     */
    public List<Map<String, Object>> getDeliveryTimeHeatmap(String city, LocalDate startDate) {
        return spatialAnalysisMapper.getDeliveryTimeHeatmap(city, startDate);
    }

    /**
     * 获取空间分布统计
     */
    public List<Map<String, Object>> getSpatialDistributionStats(String city, LocalDate startDate) {
        return spatialAnalysisMapper.getSpatialDistributionStats(city, startDate);
    }

    /**
     * 获取网格聚合数据
     */
    public List<Map<String, Object>> getGridAggregation(String city, LocalDate date, Double gridSize) {
        return spatialAnalysisMapper.getGridAggregation(city, date, gridSize);
    }

    /**
     * 获取空间汇总统计
     */
    public Map<String, Object> getSpatialSummary(String city, LocalDate startDate) {
        return spatialAnalysisMapper.getSpatialSummary(city, startDate);
    }

    /**
     * 获取配送员空间分布
     */
    public List<Map<String, Object>> getCourierSpatialDistribution(String city, LocalDate startDate, int limit) {
        return spatialAnalysisMapper.getCourierSpatialDistribution(city, startDate, limit);
    }

    /**
     * 获取城市间空间对比
     */
    public List<Map<String, Object>> getCitySpatialComparison(List<String> cities, LocalDate startDate, LocalDate endDate) {
        return spatialAnalysisMapper.getCitySpatialComparison(cities, startDate, endDate);
    }

    /**
     * 更新空间分析数据
     */
    @Transactional
    public int updateSpatialAnalysis(SpatialAnalysisMetrics metrics) {
        validateSpatialAnalysisMetrics(metrics);
        return spatialAnalysisMapper.updateSpatialAnalysis(metrics);
    }

    /**
     * 清理旧数据
     */
    @Transactional
    public int cleanupOldData(LocalDate cutoffDate) {
        return spatialAnalysisMapper.cleanupOldSpatialData(cutoffDate);
    }

    /**
     * 统计记录数
     */
    public int countByCity(String city) {
        return spatialAnalysisMapper.countByCity(city);
    }

    /**
     * 数据验证
     */
    private void validateSpatialAnalysisMetrics(SpatialAnalysisMetrics metrics) {
        if (metrics.getCity() == null || metrics.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
        if (metrics.getDate() == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (metrics.getLngGrid() == null || metrics.getLatGrid() == null) {
            throw new IllegalArgumentException("经纬度网格不能为空");
        }
        if (metrics.getDeliveryCount() == null || metrics.getDeliveryCount() < 0) {
            throw new IllegalArgumentException("配送数量不能为负数");
        }
    }

    /**
     * 实体转DTO
     */
    private SpatialAnalysisDTO convertToDTO(SpatialAnalysisMetrics metrics) {
        SpatialAnalysisDTO dto = new SpatialAnalysisDTO();
        dto.setCity(metrics.getCity());
        dto.setDate(metrics.getDate());
        dto.setLngGrid(metrics.getLngGrid() != null ? BigDecimal.valueOf(metrics.getLngGrid()) : null);
        dto.setLatGrid(metrics.getLatGrid() != null ? BigDecimal.valueOf(metrics.getLatGrid()) : null);
        dto.setDeliveryCount(metrics.getDeliveryCount());
        dto.setUniqueCouriers(metrics.getUniqueCouriers());
        dto.setAvgDeliveryTime(metrics.getAvgDeliveryTime() != null ? BigDecimal.valueOf(metrics.getAvgDeliveryTime()) : null);
        dto.setAvgDeliveryDistance(metrics.getAvgDeliveryDistance() != null ? BigDecimal.valueOf(metrics.getAvgDeliveryDistance()) : null);
        dto.setDeliveryDensity(metrics.getDeliveryDensity() != null ? BigDecimal.valueOf(metrics.getDeliveryDensity()) : null);
        return dto;
    }
}