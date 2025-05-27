package com.logistics.service.service;

import com.logistics.service.dao.entity.OperationalEfficiencyMetrics;
import com.logistics.service.dao.mapper.OperationalEfficiencyMetricsMapper;
import com.logistics.service.dto.OperationalEfficiencyDTO;
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
public class OperationalEfficiencyService {

    @Autowired
    private OperationalEfficiencyMetricsMapper operationalEfficiencyMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存运营效率数据
     */
    @Transactional
    public int saveEfficiencyMetrics(OperationalEfficiencyMetrics metrics) {
        validateEfficiencyMetrics(metrics);
        calculateDerivedMetrics(metrics);
        return operationalEfficiencyMapper.insertEfficiencyMetrics(metrics);
    }

    /**
     * 批量保存运营效率数据
     */
    @Transactional
    public int batchSaveEfficiencyMetrics(List<OperationalEfficiencyMetrics> metricsList) {
        for (OperationalEfficiencyMetrics metrics : metricsList) {
            validateEfficiencyMetrics(metrics);
            calculateDerivedMetrics(metrics);
        }
        return operationalEfficiencyMapper.batchInsertEfficiencyMetrics(metricsList);
    }

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
     * 获取指定区域的效率数据
     */
    public List<OperationalEfficiencyDTO> getEfficiencyByRegion(Integer regionId, LocalDate startDate, LocalDate endDate) {
        try {
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByRegionAndDateRange(regionId, startDate, endDate);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取区域效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 多条件查询效率数据
     */
    public List<OperationalEfficiencyDTO> getEfficiencyByConditions(String city, Integer regionId, Integer courierId,
                                                                    LocalDate startDate, LocalDate endDate) {
        try {
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByConditions(
                    city, regionId, courierId, startDate, endDate);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("多条件查询效率数据失败", e);
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
     * 获取指定日期的效率数据
     */
    public List<OperationalEfficiencyDTO> getEfficiencyByDate(String city, LocalDate date) {
        try {
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByCityAndDate(city, date);
            return metrics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取指定日期效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取城市效率趋势
     */
    public List<Map<String, Object>> getCityEfficiencyTrend(String city, LocalDate startDate) {
        return operationalEfficiencyMapper.getCityEfficiencyTrend(city, startDate);
    }

    /**
     * 获取配送员效率排行
     */
    public List<Map<String, Object>> getCourierEfficiencyRanking(String city, LocalDate startDate, int limit) {
        return operationalEfficiencyMapper.getCourierEfficiencyRanking(city, startDate, limit);
    }

    /**
     * 获取区域效率排行
     */
    public List<Map<String, Object>> getRegionEfficiencyRanking(String city, LocalDate startDate, int limit) {
        return operationalEfficiencyMapper.getRegionEfficiencyRanking(city, startDate, limit);
    }

    /**
     * 获取效率分布统计
     */
    public List<Map<String, Object>> getEfficiencyDistribution(String city, LocalDate startDate) {
        return operationalEfficiencyMapper.getEfficiencyDistribution(city, startDate);
    }

    /**
     * 获取低效率警告
     */
    public List<OperationalEfficiencyDTO> getLowEfficiencyAlerts(double threshold, LocalDate startDate, int limit) {
        List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findLowEfficiencyAlerts(threshold, startDate, limit);
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取高效率表现
     */
    public List<OperationalEfficiencyDTO> getHighEfficiencyPerformance(double threshold, LocalDate startDate, int limit) {
        List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findHighEfficiencyPerformance(threshold, startDate, limit);
        return metrics.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取运营效率汇总统计
     */
    public Map<String, Object> getEfficiencySummary(String city, LocalDate startDate) {
        return operationalEfficiencyMapper.getEfficiencySummary(city, startDate);
    }

    /**
     * 获取最新运营效率数据
     */
    public OperationalEfficiencyDTO getLatestEfficiencyByCity(String city) {
        OperationalEfficiencyMetrics metrics = operationalEfficiencyMapper.findLatestByCity(city);
        return metrics != null ? convertToDTO(metrics) : null;
    }

    /**
     * 获取配送员最新效率数据
     */
    public OperationalEfficiencyDTO getLatestEfficiencyByCourier(Integer courierId) {
        OperationalEfficiencyMetrics metrics = operationalEfficiencyMapper.findLatestByCourier(courierId);
        return metrics != null ? convertToDTO(metrics) : null;
    }

    /**
     * 更新运营效率数据
     */
    @Transactional
    public int updateEfficiencyMetrics(OperationalEfficiencyMetrics metrics) {
        validateEfficiencyMetrics(metrics);
        calculateDerivedMetrics(metrics);
        return operationalEfficiencyMapper.updateEfficiencyMetrics(metrics);
    }

    /**
     * 清理旧数据
     */
    @Transactional
    public int cleanupOldData(LocalDate cutoffDate) {
        return operationalEfficiencyMapper.cleanupOldMetrics(cutoffDate);
    }

    /**
     * 统计城市效率记录数
     */
    public int countByCity(String city) {
        return operationalEfficiencyMapper.countByCity(city);
    }

    /**
     * 统计配送员记录数
     */
    public int countByCourier(Integer courierId) {
        return operationalEfficiencyMapper.countByCourier(courierId);
    }

    /**
     * 获取城市间效率对比
     */
    public List<Map<String, Object>> getCityEfficiencyComparison(List<String> cities, LocalDate startDate, LocalDate endDate) {
        return operationalEfficiencyMapper.getCityEfficiencyComparison(cities, startDate, endDate);
    }

    /**
     * 数据验证
     */
    private void validateEfficiencyMetrics(OperationalEfficiencyMetrics metrics) {
        if (metrics.getCity() == null || metrics.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
        if (metrics.getDate() == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (metrics.getTotalOrders() == null || metrics.getTotalOrders() < 0) {
            throw new IllegalArgumentException("总订单数不能为负数");
        }
    }

    /**
     * 计算衍生指标
     */
    private void calculateDerivedMetrics(OperationalEfficiencyMetrics metrics) {
        // 计算每小时订单数
        if (metrics.getTotalOrders() != null && metrics.getTotalWorkingHours() != null && metrics.getTotalWorkingHours() > 0) {
            double ordersPerHour = metrics.getTotalOrders().doubleValue() / metrics.getTotalWorkingHours();
            metrics.setOrdersPerHour(ordersPerHour);
        }

        // 计算单均距离
        if (metrics.getTotalDistance() != null && metrics.getTotalOrders() != null && metrics.getTotalOrders() > 0) {
            double distancePerOrder = metrics.getTotalDistance() / metrics.getTotalOrders();
            metrics.setDistancePerOrder(distancePerOrder);
        }

        // 计算效率评分（示例算法）
        if (metrics.getOrdersPerHour() != null && metrics.getAvgDeliveryTime() != null) {
            double efficiencyScore = Math.min(100, (metrics.getOrdersPerHour() * 10) / (metrics.getAvgDeliveryTime() / 60));
            metrics.setEfficiencyScore(efficiencyScore);
        }
    }

    /**
     * 实体转DTO
     */
    private OperationalEfficiencyDTO convertToDTO(OperationalEfficiencyMetrics metrics) {
        OperationalEfficiencyDTO dto = new OperationalEfficiencyDTO();
        dto.setCity(metrics.getCity());
        dto.setRegionId(metrics.getRegionId());
        dto.setCourierId(metrics.getCourierId());
        dto.setDate(metrics.getDate());
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