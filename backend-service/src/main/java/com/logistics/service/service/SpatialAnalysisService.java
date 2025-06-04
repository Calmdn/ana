package com.logistics.service.service;

import com.logistics.service.dao.entity.SpatialAnalysisMetrics;
import com.logistics.service.dao.mapper.SpatialAnalysisMetricsMapper;
import com.logistics.service.dto.SpatialAnalysisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SpatialAnalysisService {

    @Autowired
    private SpatialAnalysisMetricsMapper spatialAnalysisMapper;

    // ==================== 数据保存操作 ====================

    /**
     * 保存空间分析数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"spatial", "stats"}, allEntries = true)
    public int saveSpatialAnalysis(SpatialAnalysisMetrics metrics) {
        validateSpatialAnalysisMetrics(metrics);

        int result = spatialAnalysisMapper.insertSpatialAnalysis(metrics);
        if (result > 0) {
            log.info("保存空间分析数据成功，城市: {}，已清除缓存", metrics.getCity());
        }
        return result;
    }

    /**
     * 批量保存空间分析数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"spatial", "stats"}, allEntries = true)
    public int batchSaveSpatialAnalysis(List<SpatialAnalysisMetrics> metricsList) {
        for (SpatialAnalysisMetrics metrics : metricsList) {
            validateSpatialAnalysisMetrics(metrics);
        }

        int result = spatialAnalysisMapper.batchInsertSpatialAnalysis(metricsList);
        if (result > 0) {
            log.info("批量保存空间分析数据成功，共保存 {} 条，已清除缓存", result);
        }
        return result;
    }

    /**
     * 更新空间分析数据 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"spatial", "stats"}, allEntries = true)
    public int updateSpatialAnalysis(SpatialAnalysisMetrics metrics) {
        validateSpatialAnalysisMetrics(metrics);

        int result = spatialAnalysisMapper.updateSpatialAnalysis(metrics);
        if (result > 0) {
            log.info(" 更新空间分析数据成功，已清除缓存");
        }
        return result;
    }

    // ==================== 查询操作 ====================

    /**
     * 获取指定城市的空间分析数据 - 添加缓存
     */
    @Cacheable(value = "spatial", key = "'city:' + #city + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<SpatialAnalysisDTO> getSpatialAnalysisByCity(String city, LocalDate startDate, LocalDate endDate) {
        try {
            log.info(" 查询数据库获取空间分析[city={}]", city);
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findByCityAndDateRange(city, startDate, endDate);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取空间分析数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据城市和日期获取空间分析 - 添加缓存
     */
    @Cacheable(value = "spatial", key = "'date:' + #city + ':' + #date", unless = "#result.isEmpty()")
    public List<SpatialAnalysisDTO> getSpatialAnalysisByDate(String city, LocalDate date) {
        try {
            log.info(" 查询数据库获取指定日期空间分析[city={}, date={}]", city, date);
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findByCityAndDate(city, date);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据日期获取空间分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据地理范围获取空间分析 - 添加缓存
     */
    @Cacheable(value = "spatial",
            key = "'geo_range:' + #city + ':' + #minLng + ':' + #maxLng + ':' + #minLat + ':' + #maxLat + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<SpatialAnalysisDTO> getSpatialAnalysisByGeoRange(String city, Double minLng, Double maxLng,
                                                                 Double minLat, Double maxLat,
                                                                 LocalDate startDate, LocalDate endDate) {
        try {
            log.info(" 查询数据库获取地理范围空间分析[city={}]", city);
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findByGeoRange(
                    city, minLng, maxLng, minLat, maxLat, startDate, endDate);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据地理范围获取空间分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取热点区域分析 - 添加缓存
     */
    @Cacheable(value = "spatial", key = "'hotspot:' + #city + ':' + #date", unless = "#result.isEmpty()")
    public List<SpatialAnalysisDTO> getHotspotAnalysis(String city, LocalDate date) {
        return getHotspotAnalysis(city, date, 20);
    }

    /**
     * 获取热点区域分析（指定数量）- 添加缓存
     */
    @Cacheable(value = "spatial", key = "'hotspot:' + #city + ':' + #date + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<SpatialAnalysisDTO> getHotspotAnalysis(String city, LocalDate date, int limit) {
        try {
            log.info("查询数据库获取热点区域分析[city={}, limit={}]", city, limit);
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findHotspotsByCity(city, date, limit);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取热点区域分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取密度分析数据 - 添加缓存
     */
    @Cacheable(value = "spatial", key = "'density:' + #city + ':' + #date", unless = "#result.isEmpty()")
    public List<SpatialAnalysisDTO> getDensityAnalysis(String city, LocalDate date) {
        try {
            log.info(" 查询数据库获取密度分析[city={}, date={}]", city, date);
            List<SpatialAnalysisMetrics> metrics = spatialAnalysisMapper.findDensityAnalysisByCity(city, date);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取密度分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日空间分析 - 添加缓存
     */
    @Cacheable(value = "spatial", key = "'today:' + #city", unless = "#result.isEmpty()")
    public List<SpatialAnalysisDTO> getTodaySpatialAnalysis(String city) {
        LocalDate today = LocalDate.now();
        return getSpatialAnalysisByDate(city, today);
    }

    // ==================== 统计分析操作 ====================

    /**
     * 获取配送密度热点 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'delivery_hotspots:' + #city + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getDeliveryDensityHotspots(String city, LocalDate startDate, int limit) {
        log.info(" 查询数据库获取配送密度热点[city={}]", city);
        return spatialAnalysisMapper.getDeliveryDensityHotspots(city, startDate, limit);
    }

    /**
     * 获取配送时间热图数据 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'time_heatmap:' + #city + ':' + #startDate", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getDeliveryTimeHeatmap(String city, LocalDate startDate) {
        log.info(" 查询数据库获取配送时间热图[city={}]", city);
        return spatialAnalysisMapper.getDeliveryTimeHeatmap(city, startDate);
    }

    /**
     * 获取空间分布统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'distribution_stats:' + #city + ':' + #startDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getSpatialDistributionStats(String city, LocalDate startDate) {
        log.info(" 查询数据库获取空间分布统计[city={}]", city);
        return spatialAnalysisMapper.getSpatialDistributionStats(city, startDate);
    }

    /**
     * 获取网格聚合数据 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'grid_aggregation:' + #city + ':' + #date + ':' + #gridSize",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getGridAggregation(String city, LocalDate date, Double gridSize) {
        log.info(" 查询数据库获取网格聚合[city={}, gridSize={}]", city, gridSize);
        return spatialAnalysisMapper.getGridAggregation(city, date, gridSize);
    }

    /**
     * 获取空间汇总统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'summary:' + #city + ':' + #startDate", unless = "#result == null")
    public Map<String, Object> getSpatialSummary(String city, LocalDate startDate) {
        log.info(" 查询数据库获取空间汇总[city={}]", city);
        return spatialAnalysisMapper.getSpatialSummary(city, startDate);
    }

    /**
     * 获取配送员空间分布 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'courier_distribution:' + #city + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCourierSpatialDistribution(String city, LocalDate startDate, int limit) {
        log.info(" 查询数据库获取配送员空间分布[city={}]", city);
        return spatialAnalysisMapper.getCourierSpatialDistribution(city, startDate, limit);
    }

    /**
     * 获取城市间空间对比 - 添加缓存
     */
    @Cacheable(value = "stats",
            key = "'comparison:' + #cities.toString() + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCitySpatialComparison(List<String> cities, LocalDate startDate, LocalDate endDate) {
        log.info(" 查询数据库获取城市空间对比，城市数: {}", cities.size());
        return spatialAnalysisMapper.getCitySpatialComparison(cities, startDate, endDate);
    }

    /**
     * 统计记录数 - 不缓存（简单计数查询）
     */
    public int countByCity(String city) {
        return spatialAnalysisMapper.countByCity(city);
    }

    // ==================== 数据维护 ====================

    /**
     * 清理旧数据 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"spatial", "stats"}, allEntries = true)
    public int cleanupOldData(LocalDate cutoffDate) {
        int result = spatialAnalysisMapper.cleanupOldSpatialData(cutoffDate);
        if (result > 0) {
            log.info("清理旧空间分析数据成功，删除 {} 条记录，已清除缓存", result);
        }
        return result;
    }

    // ==================== 私有方法 ====================

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