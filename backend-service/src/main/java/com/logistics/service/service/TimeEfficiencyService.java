package com.logistics.service.service;

import com.logistics.service.dao.entity.TimeEfficiencyMetrics;
import com.logistics.service.dao.mapper.TimeEfficiencyMetricsMapper;
import com.logistics.service.dto.TimeEfficiencyDTO;
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
public class TimeEfficiencyService {

    @Autowired
    private TimeEfficiencyMetricsMapper timeEfficiencyMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存时间效率数据
     */
    @Transactional
    public int saveTimeEfficiency(TimeEfficiencyMetrics metrics) {
        validateTimeEfficiencyMetrics(metrics);
        calculateDerivedMetrics(metrics);
        return timeEfficiencyMapper.insertTimeEfficiency(metrics);
    }

    /**
     * 批量保存时间效率数据
     */
    @Transactional
    public int batchSaveTimeEfficiency(List<TimeEfficiencyMetrics> metricsList) {
        for (TimeEfficiencyMetrics metrics : metricsList) {
            validateTimeEfficiencyMetrics(metrics);
            calculateDerivedMetrics(metrics);
        }
        return timeEfficiencyMapper.batchInsertTimeEfficiency(metricsList);
    }

    /**
     * 获取指定城市的时间效率数据
     */
    public List<TimeEfficiencyDTO> getTimeEfficiencyByCity(String city, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("time_efficiency:%s:%s:%s", city, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<TimeEfficiencyDTO> cache = (List<TimeEfficiencyDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取时间效率[city={}], size={}", city, cache.size());
                return cache;
            }

            log.info("🔍 Redis 未命中，查询 MySQL 时间效率[city={}]", city);
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findByCityAndDateRange(city, startDate, endDate);

            List<TimeEfficiencyDTO> result = metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 45, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存时间效率[city={}]，ttl=45m", city);
            }

            return result;
        } catch (Exception e) {
            log.error("获取时间效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据城市和日期获取时间效率
     */
    public List<TimeEfficiencyDTO> getTimeEfficiencyByDate(String city, LocalDate date) {
        try {
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findByCityAndDate(city, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据日期获取时间效率失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 多条件查询时间效率
     */
    public List<TimeEfficiencyDTO> getTimeEfficiencyByConditions(String city, LocalDate startDate, LocalDate endDate,
                                                                 Double minFastRate, Double maxSlowRate) {
        try {
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findByConditions(
                    city, startDate, endDate, minFastRate, maxSlowRate);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("多条件查询时间效率失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日时间效率
     */
    public List<TimeEfficiencyDTO> getTodayTimeEfficiency(String city) {
        LocalDate today = LocalDate.now();
        return getTimeEfficiencyByDate(city, today);
    }

    /**
     * 获取配送效率趋势
     */
    public List<TimeEfficiencyDTO> getDeliveryEfficiencyTrend(String city, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        return getTimeEfficiencyByCity(city, startDate, endDate);
    }

    /**
     * 获取配送效率趋势统计
     */
    public List<Map<String, Object>> getDeliveryEfficiencyTrendStats(String city, LocalDate startDate) {
        return timeEfficiencyMapper.getDeliveryEfficiencyTrend(city, startDate);
    }

    /**
     * 获取效率分布统计
     */
    public List<Map<String, Object>> getEfficiencyDistribution(String city, LocalDate startDate) {
        return timeEfficiencyMapper.getEfficiencyDistribution(city, startDate);
    }

    /**
     * 获取时间效率排行
     */
    public List<Map<String, Object>> getTimeEfficiencyRanking(List<String> cities, LocalDate startDate, int limit) {
        return timeEfficiencyMapper.getTimeEfficiencyRanking(cities, startDate, limit);
    }

    /**
     * 获取慢配送分析
     */
    public List<TimeEfficiencyDTO> getSlowDeliveryAnalysis(String city, double threshold, LocalDate startDate, int limit) {
        List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findSlowDeliveryAnalysis(city, threshold, startDate, limit);
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取快速配送分析
     */
    public List<TimeEfficiencyDTO> getFastDeliveryAnalysis(String city, double threshold, LocalDate startDate, int limit) {
        List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findFastDeliveryAnalysis(city, threshold, startDate, limit);
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取时间效率汇总统计
     */
    public Map<String, Object> getTimeEfficiencySummary(String city, LocalDate startDate) {
        return timeEfficiencyMapper.getTimeEfficiencySummary(city, startDate);
    }

    /**
     * 获取最新时间效率数据
     */
    public TimeEfficiencyDTO getLatestTimeEfficiency(String city) {
        TimeEfficiencyMetrics metrics = timeEfficiencyMapper.findLatestByCity(city);
        return metrics != null ? convertToDTO(metrics) : null;
    }

    /**
     * 获取城市间时间效率对比
     */
    public List<Map<String, Object>> getCityTimeEfficiencyComparison(List<String> cities, LocalDate startDate, LocalDate endDate) {
        return timeEfficiencyMapper.getCityTimeEfficiencyComparison(cities, startDate, endDate);
    }

    /**
     * 更新时间效率数据
     */
    @Transactional
    public int updateTimeEfficiency(TimeEfficiencyMetrics metrics) {
        validateTimeEfficiencyMetrics(metrics);
        calculateDerivedMetrics(metrics);
        return timeEfficiencyMapper.updateTimeEfficiency(metrics);
    }

    /**
     * 清理旧数据
     */
    @Transactional
    public int cleanupOldData(LocalDate cutoffDate) {
        return timeEfficiencyMapper.cleanupOldTimeEfficiency(cutoffDate);
    }

    /**
     * 统计记录数
     */
    public int countByCity(String city) {
        return timeEfficiencyMapper.countByCity(city);
    }

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