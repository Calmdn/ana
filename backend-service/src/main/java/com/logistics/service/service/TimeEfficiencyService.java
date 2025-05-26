package com.logistics.service.service;

import com.logistics.service.dao.entity.TimeEfficiencyMetrics;
import com.logistics.service.dao.mapper.TimeEfficiencyMetricsMapper;
import com.logistics.service.dto.TimeEfficiencyDTO;
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
public class TimeEfficiencyService {

    @Autowired
    private TimeEfficiencyMetricsMapper timeEfficiencyMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
     * 获取小时级时间效率分析
     */
    public List<TimeEfficiencyDTO> getHourlyTimeEfficiency(String city, LocalDate date) {
        try {
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findByCityAndDateGroupByHour(city, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取小时时间效率失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日时间效率
     */
    public List<TimeEfficiencyDTO> getTodayTimeEfficiency(String city) {
        LocalDate today = LocalDate.now();
        return getTimeEfficiencyByCity(city, today, today);
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
     * 获取取件效率分析
     */
    public List<TimeEfficiencyDTO> getPickupEfficiencyAnalysis(String city, LocalDate startDate, LocalDate endDate) {
        try {
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findPickupAnalysisByCity(city, startDate, endDate);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取取件效率分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取高峰时段分析
     */
    public List<TimeEfficiencyDTO> getPeakHourAnalysis(String city, LocalDate date) {
        try {
            List<TimeEfficiencyMetrics> metrics = timeEfficiencyMapper.findPeakHourAnalysis(city, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取高峰时段分析失败", e);
            return new ArrayList<>();
        }
    }

    private TimeEfficiencyDTO convertToDTO(TimeEfficiencyMetrics metrics) {
        TimeEfficiencyDTO dto = new TimeEfficiencyDTO();
        dto.setCity(metrics.getCity());
        dto.setAnalysisDate(metrics.getAnalysisDate());
        dto.setAnalysisHour(metrics.getAnalysisHour());
        dto.setTotalDeliveries(metrics.getTotalDeliveries());
        dto.setAvgDeliveryTime(metrics.getAvgDeliveryTime() != null ? BigDecimal.valueOf(metrics.getAvgDeliveryTime()) : null);
        dto.setMedianDeliveryTime(metrics.getMedianDeliveryTime() != null ? BigDecimal.valueOf(metrics.getMedianDeliveryTime()) : null);
        dto.setP95DeliveryTime(metrics.getP95DeliveryTime() != null ? BigDecimal.valueOf(metrics.getP95DeliveryTime()) : null);
        dto.setFastDeliveries(metrics.getFastDeliveries());
        dto.setNormalDeliveries(metrics.getNormalDeliveries());
        dto.setSlowDeliveries(metrics.getSlowDeliveries());
        dto.setFastDeliveryRate(metrics.getFastDeliveryRate() != null ? BigDecimal.valueOf(metrics.getFastDeliveryRate()) : null);
        dto.setSlowDeliveryRate(metrics.getSlowDeliveryRate() != null ? BigDecimal.valueOf(metrics.getSlowDeliveryRate()) : null);
        dto.setTotalPickups(metrics.getTotalPickups());
        dto.setAvgPickupTime(metrics.getAvgPickupTime() != null ? BigDecimal.valueOf(metrics.getAvgPickupTime()) : null);
        dto.setMedianPickupTime(metrics.getMedianPickupTime() != null ? BigDecimal.valueOf(metrics.getMedianPickupTime()) : null);
        dto.setP95PickupTime(metrics.getP95PickupTime() != null ? BigDecimal.valueOf(metrics.getP95PickupTime()) : null);
        dto.setFastPickups(metrics.getFastPickups());
        dto.setNormalPickups(metrics.getNormalPickups());
        dto.setSlowPickups(metrics.getSlowPickups());
        dto.setFastPickupRate(metrics.getFastPickupRate() != null ? BigDecimal.valueOf(metrics.getFastPickupRate()) : null);
        dto.setSlowPickupRate(metrics.getSlowPickupRate() != null ? BigDecimal.valueOf(metrics.getSlowPickupRate()) : null);
        dto.setOnTimePickups(metrics.getOnTimePickups());
        dto.setOnTimePickupRate(metrics.getOnTimePickupRate() != null ? BigDecimal.valueOf(metrics.getOnTimePickupRate()) : null);
        return dto;
    }
}