package com.logistics.service.service;

import com.logistics.service.dao.entity.TimeEfficiencyMetrics;
import com.logistics.service.dao.mapper.TimeEfficiencyMetricsMapper;
import com.logistics.service.dto.TimeEfficiencyDTO;
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
public class TimeEfficiencyService {

    @Autowired
    private TimeEfficiencyMetricsMapper timeEfficiencyMapper;

    // ==================== 数据保存操作 ====================

    /**
     * 保存时间效率数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"time_efficiency", "stats"}, allEntries = true)
    public int saveTimeEfficiency(TimeEfficiencyMetrics metrics) {
        validateTimeEfficiencyMetrics(metrics);
        calculateDerivedMetrics(metrics);

        int result = timeEfficiencyMapper.insertTimeEfficiency(metrics);
        if (result > 0) {
            log.info("✅ 保存时间效率数据成功，城市: {}，已清除缓存", metrics.getCity());
        }
        return result;
    }

    /**
     * 批量保存时间效率数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"time_efficiency", "stats"}, allEntries = true)
    public int batchSaveTimeEfficiency(List<TimeEfficiencyMetrics> metricsList) {
        for (TimeEfficiencyMetrics metrics : metricsList) {
            validateTimeEfficiencyMetrics(metrics);
            calculateDerivedMetrics(metrics);
        }

        int result = timeEfficiencyMapper.batchInsertTimeEfficiency(metricsList);
        if (result > 0) {
            log.info("✅ 批量保存时间效率数据成功，共保存 {} 条，已清除缓存", result);
        }
        return result;
    }

    /**
     * 更新时间效率数据 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"time_efficiency", "stats"}, allEntries = true)
    public int updateTimeEfficiency(TimeEfficiencyMetrics metrics) {
        validateTimeEfficiencyMetrics(metrics);
        calculateDerivedMetrics(metrics);

        int result = timeEfficiencyMapper.updateTimeEfficiency(metrics);
        if (result > 0) {
            log.info("✅ 更新时间效率数据成功，已清除缓存");
        }
        return result;
    }

    // ==================== 查询操作 ====================

    /**
     * 获取指定城市的时间效率数据 - 添加缓存
     */
    @Cacheable(value = "time_efficiency", key = "'city:' + #city + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<TimeEfficiencyDTO> getTimeEfficiencyByCity(String city, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("🔍 查询数据库获取时间效率[city={}]", city);
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findByCityAndDateRange(city, startDate, endDate);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取时间效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据城市和日期获取时间效率 - 添加缓存
     */
    @Cacheable(value = "time_efficiency", key = "'date:' + #city + ':' + #date", unless = "#result.isEmpty()")
    public List<TimeEfficiencyDTO> getTimeEfficiencyByDate(String city, LocalDate date) {
        try {
            log.info("🔍 查询数据库获取指定日期时间效率[city={}, date={}]", city, date);
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findByCityAndDate(city, date);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据日期获取时间效率失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 多条件查询时间效率 - 添加缓存
     */
    @Cacheable(value = "time_efficiency",
            key = "'conditions:' + #city + ':' + #startDate + ':' + #endDate + ':' + #minFastRate + ':' + #maxSlowRate",
            unless = "#result.isEmpty()")
    public List<TimeEfficiencyDTO> getTimeEfficiencyByConditions(String city, LocalDate startDate, LocalDate endDate,
                                                                 Double minFastRate, Double maxSlowRate) {
        try {
            log.info("🔍 查询数据库获取多条件时间效率[city={}]", city);
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findByConditions(
                    city, startDate, endDate, minFastRate, maxSlowRate);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("多条件查询时间效率失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日时间效率 - 添加缓存
     */
    @Cacheable(value = "time_efficiency", key = "'today:' + #city", unless = "#result.isEmpty()")
    public List<TimeEfficiencyDTO> getTodayTimeEfficiency(String city) {
        LocalDate today = LocalDate.now();
        return getTimeEfficiencyByDate(city, today);
    }

    /**
     * 获取配送效率趋势 - 添加缓存
     */
    @Cacheable(value = "time_efficiency", key = "'trend:' + #city + ':' + #days", unless = "#result.isEmpty()")
    public List<TimeEfficiencyDTO> getDeliveryEfficiencyTrend(String city, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        return getTimeEfficiencyByCity(city, startDate, endDate);
    }

    /**
     * 获取慢配送分析 - 添加缓存
     */
    @Cacheable(value = "time_efficiency",
            key = "'slow_delivery:' + #city + ':' + #threshold + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<TimeEfficiencyDTO> getSlowDeliveryAnalysis(String city, double threshold, LocalDate startDate, int limit) {
        log.info("🔍 查询数据库获取慢配送分析[city={}, threshold={}]", city, threshold);
        List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findSlowDeliveryAnalysis(city, threshold, startDate, limit);
        return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 获取快速配送分析 - 添加缓存
     */
    @Cacheable(value = "time_efficiency",
            key = "'fast_delivery:' + #city + ':' + #threshold + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<TimeEfficiencyDTO> getFastDeliveryAnalysis(String city, double threshold, LocalDate startDate, int limit) {
        log.info("🔍 查询数据库获取快速配送分析[city={}, threshold={}]", city, threshold);
        List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findFastDeliveryAnalysis(city, threshold, startDate, limit);
        return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 获取最新时间效率数据 - 添加缓存
     */
    @Cacheable(value = "time_efficiency", key = "'latest:' + #city", unless = "#result == null")
    public TimeEfficiencyDTO getLatestTimeEfficiency(String city) {
        log.info("🔍 查询数据库获取最新时间效率[city={}]", city);
        TimeEfficiencyMetrics metrics = timeEfficiencyMapper.findLatestByCity(city);
        return metrics != null ? convertToDTO(metrics) : null;
    }

    // ==================== 统计分析操作 ====================

    /**
     * 获取配送效率趋势统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'trend_stats:' + #city + ':' + #startDate", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getDeliveryEfficiencyTrendStats(String city, LocalDate startDate) {
        log.info("🔍 查询数据库获取配送效率趋势统计[city={}]", city);
        return timeEfficiencyMapper.getDeliveryEfficiencyTrend(city, startDate);
    }

    /**
     * 获取效率分布统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'distribution:' + #city + ':' + #startDate", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getEfficiencyDistribution(String city, LocalDate startDate) {
        log.info("🔍 查询数据库获取效率分布统计[city={}]", city);
        return timeEfficiencyMapper.getEfficiencyDistribution(city, startDate);
    }

    /**
     * 获取时间效率排行 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'ranking:' + #cities.toString() + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getTimeEfficiencyRanking(List<String> cities, LocalDate startDate, int limit) {
        log.info("🔍 查询数据库获取时间效率排行，城市数: {}", cities.size());
        return timeEfficiencyMapper.getTimeEfficiencyRanking(cities, startDate, limit);
    }

    /**
     * 获取时间效率汇总统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'summary:' + #city + ':' + #startDate", unless = "#result == null")
    public Map<String, Object> getTimeEfficiencySummary(String city, LocalDate startDate) {
        log.info("🔍 查询数据库获取时间效率汇总[city={}]", city);
        return timeEfficiencyMapper.getTimeEfficiencySummary(city, startDate);
    }

    /**
     * 获取城市间时间效率对比 - 添加缓存
     */
    @Cacheable(value = "stats",
            key = "'comparison:' + #cities.toString() + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCityTimeEfficiencyComparison(List<String> cities, LocalDate startDate, LocalDate endDate) {
        log.info("🔍 查询数据库获取城市时间效率对比，城市数: {}", cities.size());
        return timeEfficiencyMapper.getCityTimeEfficiencyComparison(cities, startDate, endDate);
    }

    /**
     * 统计记录数 - 不缓存（简单计数查询）
     */
    public int countByCity(String city) {
        return timeEfficiencyMapper.countByCity(city);
    }

    // ==================== 数据维护 ====================

    /**
     * 清理旧数据 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"time_efficiency", "stats"}, allEntries = true)
    public int cleanupOldData(LocalDate cutoffDate) {
        int result = timeEfficiencyMapper.cleanupOldTimeEfficiency(cutoffDate);
        if (result > 0) {
            log.info("✅ 清理旧时间效率数据成功，删除 {} 条记录，已清除缓存", result);
        }
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 数据验证
     */
    private void validateTimeEfficiencyMetrics(TimeEfficiencyMetrics metrics) {
        if (metrics.getCity() == null || metrics.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
        if (metrics.getDate() == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (metrics.getTotalDeliveries() == null || metrics.getTotalDeliveries() < 0) {
            throw new IllegalArgumentException("总配送数量不能为负数");
        }
    }

    /**
     * 计算衍生指标
     */
    private void calculateDerivedMetrics(TimeEfficiencyMetrics metrics) {
        // 计算快速配送率
        if (metrics.getFastDeliveries() != null && metrics.getTotalDeliveries() != null && metrics.getTotalDeliveries() > 0) {
            double fastRate = metrics.getFastDeliveries().doubleValue() / metrics.getTotalDeliveries();
            metrics.setFastDeliveryRate(fastRate);
        }

        // 计算慢速配送率
        if (metrics.getSlowDeliveries() != null && metrics.getTotalDeliveries() != null && metrics.getTotalDeliveries() > 0) {
            double slowRate = metrics.getSlowDeliveries().doubleValue() / metrics.getTotalDeliveries();
            metrics.setSlowDeliveryRate(slowRate);
        }
    }

    /**
     * 实体转DTO
     */
    private TimeEfficiencyDTO convertToDTO(TimeEfficiencyMetrics metrics) {
        TimeEfficiencyDTO dto = new TimeEfficiencyDTO();
        dto.setCity(metrics.getCity());
        dto.setDate(metrics.getDate());
        dto.setTotalDeliveries(metrics.getTotalDeliveries());
        dto.setAvgDeliveryTime(metrics.getAvgDeliveryTime() != null ? BigDecimal.valueOf(metrics.getAvgDeliveryTime()) : null);
        dto.setFastDeliveries(metrics.getFastDeliveries());
        dto.setSlowDeliveries(metrics.getSlowDeliveries());
        dto.setFastDeliveryRate(metrics.getFastDeliveryRate() != null ? BigDecimal.valueOf(metrics.getFastDeliveryRate()) : null);
        dto.setSlowDeliveryRate(metrics.getSlowDeliveryRate() != null ? BigDecimal.valueOf(metrics.getSlowDeliveryRate()) : null);
        return dto;
    }
}