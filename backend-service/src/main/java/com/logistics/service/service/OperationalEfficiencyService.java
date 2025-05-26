package com.logistics.service.service;

import com.logistics.service.dao.entity.OperationalEfficiencyMetrics;
import com.logistics.service.dao.mapper.OperationalEfficiencyMetricsMapper;
import com.logistics.service.dto.OperationalEfficiencyDTO;
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
public class OperationalEfficiencyService {

    @Autowired
    private OperationalEfficiencyMetricsMapper operationalEfficiencyMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取指定城市的运营效率数据
     */
    public List<OperationalEfficiencyDTO> getEfficiencyByCity(String city, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("operational_efficiency:%s:%s:%s", city, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<OperationalEfficiencyDTO> cache = (List<OperationalEfficiencyDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取运营效率[city={}], size={}", city, cache.size());
                return cache;
            }

            log.info("🔍 Redis 未命中，查询 MySQL 运营效率[city={}]", city);
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByCityAndDateRange(city, startDate, endDate);

            List<OperationalEfficiencyDTO> result = metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 45, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存运营效率[city={}]，ttl=45m", city);
            }

            return result;
        } catch (Exception e) {
            log.error("获取运营效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定配送员的效率数据
     */
    public List<OperationalEfficiencyDTO> getEfficiencyByCourier(Integer courierId, LocalDate startDate, LocalDate endDate) {
        try {
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByCourierAndDateRange(courierId, startDate, endDate);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取配送员效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日运营效率
     */
    public List<OperationalEfficiencyDTO> getTodayEfficiency(String city) {
        LocalDate today = LocalDate.now();
        return getEfficiencyByCity(city, today, today);
    }

    /**
     * 获取小时级别效率数据
     */
    public List<OperationalEfficiencyDTO> getHourlyEfficiency(String city, LocalDate date) {
        try {
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByCityAndDateGroupByHour(city, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取小时效率数据失败", e);
            return new ArrayList<>();
        }
    }

    private OperationalEfficiencyDTO convertToDTO(OperationalEfficiencyMetrics metrics) {
        OperationalEfficiencyDTO dto = new OperationalEfficiencyDTO();
        dto.setCity(metrics.getCity());
        dto.setRegionId(metrics.getRegionId());
        dto.setCourierId(metrics.getCourierId());
        dto.setAnalysisDate(metrics.getAnalysisDate());
        dto.setAnalysisHour(metrics.getAnalysisHour());
        dto.setTotalOrders(metrics.getTotalOrders());
        dto.setUniqueAoiServed(metrics.getUniqueAoiServed());
        dto.setTotalDistance(metrics.getTotalDistance() != null ? BigDecimal.valueOf(metrics.getTotalDistance()) : null);
        dto.setTotalWorkingHours(metrics.getTotalWorkingHours() != null ? BigDecimal.valueOf(metrics.getTotalWorkingHours()) : null);
        dto.setAvgDeliveryTime(metrics.getAvgDeliveryTime() != null ? BigDecimal.valueOf(metrics.getAvgDeliveryTime()) : null);
        dto.setOrdersPerHour(metrics.getOrdersPerHour() != null ? BigDecimal.valueOf(metrics.getOrdersPerHour()) : null);
        dto.setDistancePerOrder(metrics.getDistancePerOrder() != null ? BigDecimal.valueOf(metrics.getDistancePerOrder()) : null);
        dto.setEfficiencyScore(metrics.getEfficiencyScore() != null ? BigDecimal.valueOf(metrics.getEfficiencyScore()) : null);
        return dto;
    }
}