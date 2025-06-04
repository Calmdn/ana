package com.logistics.service.service;

import com.logistics.service.dao.entity.OperationalEfficiencyMetrics;
import com.logistics.service.dao.mapper.OperationalEfficiencyMetricsMapper;
import com.logistics.service.dto.OperationalEfficiencyDTO;
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
public class OperationalEfficiencyService {

    @Autowired
    private OperationalEfficiencyMetricsMapper operationalEfficiencyMapper;

    // ==================== 数据保存操作 ====================

    /**
     * 保存运营效率数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"efficiency", "stats"}, allEntries = true)
    public int saveEfficiencyMetrics(OperationalEfficiencyMetrics metrics) {
        validateEfficiencyMetrics(metrics);
        calculateDerivedMetrics(metrics);

        int result = operationalEfficiencyMapper.insertEfficiencyMetrics(metrics);
        if (result > 0) {
            log.info("保存运营效率数据成功，城市: {}，已清除缓存", metrics.getCity());
        }
        return result;
    }

    /**
     * 批量保存运营效率数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"efficiency", "stats"}, allEntries = true)
    public int batchSaveEfficiencyMetrics(List<OperationalEfficiencyMetrics> metricsList) {
        for (OperationalEfficiencyMetrics metrics : metricsList) {
            validateEfficiencyMetrics(metrics);
            calculateDerivedMetrics(metrics);
        }

        int result = operationalEfficiencyMapper.batchInsertEfficiencyMetrics(metricsList);
        if (result > 0) {
            log.info("批量保存运营效率数据成功，共保存 {} 条，已清除缓存", result);
        }
        return result;
    }

    /**
     * 更新运营效率数据 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"efficiency", "stats"}, allEntries = true)
    public int updateEfficiencyMetrics(OperationalEfficiencyMetrics metrics) {
        validateEfficiencyMetrics(metrics);
        calculateDerivedMetrics(metrics);

        int result = operationalEfficiencyMapper.updateEfficiencyMetrics(metrics);
        if (result > 0) {
            log.info("更新运营效率数据成功，已清除缓存");
        }
        return result;
    }

    // ==================== 查询操作 ====================

    /**
     * 获取指定城市的运营效率数据 - 添加缓存
     */
    @Cacheable(value = "efficiency", key = "'city:' + #city + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<OperationalEfficiencyDTO> getEfficiencyByCity(String city, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("查询数据库获取运营效率[city={}]", city);
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByCityAndDateRange(city, startDate, endDate);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取运营效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定配送员的效率数据 - 不缓存（个人敏感数据）
     */
    public List<OperationalEfficiencyDTO> getEfficiencyByCourier(Integer courierId, LocalDate startDate, LocalDate endDate) {
        try {
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByCourierAndDateRange(courierId, startDate, endDate);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取配送员效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定区域的效率数据 - 添加缓存
     */
    @Cacheable(value = "efficiency", key = "'region:' + #regionId + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<OperationalEfficiencyDTO> getEfficiencyByRegion(Integer regionId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("查询数据库获取区域效率[regionId={}]", regionId);
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByRegionAndDateRange(regionId, startDate, endDate);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取区域效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 多条件查询效率数据 - 添加缓存
     */
    @Cacheable(value = "efficiency",
            key = "'conditions:' + #city + ':' + #regionId + ':' + #courierId + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<OperationalEfficiencyDTO> getEfficiencyByConditions(String city, Integer regionId, Integer courierId,
                                                                    LocalDate startDate, LocalDate endDate) {
        try {
            log.info("查询数据库获取多条件效率[city={}]", city);
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByConditions(
                    city, regionId, courierId, startDate, endDate);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("多条件查询效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日运营效率 - 添加缓存
     */
    @Cacheable(value = "efficiency", key = "'today:' + #city", unless = "#result.isEmpty()")
    public List<OperationalEfficiencyDTO> getTodayEfficiency(String city) {
        LocalDate today = LocalDate.now();
        return getEfficiencyByCity(city, today, today);
    }

    /**
     * 获取指定日期的效率数据 - 添加缓存
     */
    @Cacheable(value = "efficiency", key = "'date:' + #city + ':' + #date", unless = "#result.isEmpty()")
    public List<OperationalEfficiencyDTO> getEfficiencyByDate(String city, LocalDate date) {
        try {
            log.info("查询数据库获取指定日期效率[city={}, date={}]", city, date);
            List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findByCityAndDate(city, date);
            return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取指定日期效率数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取低效率警告 - 添加缓存
     */
    @Cacheable(value = "efficiency", key = "'low_alerts:' + #threshold + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<OperationalEfficiencyDTO> getLowEfficiencyAlerts(double threshold, LocalDate startDate, int limit) {
        log.info("查询数据库获取低效率警告[threshold={}]", threshold);
        List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findLowEfficiencyAlerts(threshold, startDate, limit);
        return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 获取高效率表现 - 添加缓存
     */
    @Cacheable(value = "efficiency", key = "'high_performance:' + #threshold + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<OperationalEfficiencyDTO> getHighEfficiencyPerformance(double threshold, LocalDate startDate, int limit) {
        log.info("查询数据库获取高效率表现[threshold={}]", threshold);
        List<OperationalEfficiencyMetrics> metrics = operationalEfficiencyMapper.findHighEfficiencyPerformance(threshold, startDate, limit);
        return metrics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 获取最新运营效率数据 - 添加缓存
     */
    @Cacheable(value = "efficiency", key = "'latest:' + #city", unless = "#result == null")
    public OperationalEfficiencyDTO getLatestEfficiencyByCity(String city) {
        log.info("查询数据库获取最新效率[city={}]", city);
        OperationalEfficiencyMetrics metrics = operationalEfficiencyMapper.findLatestByCity(city);
        return metrics != null ? convertToDTO(metrics) : null;
    }

    /**
     * 获取配送员最新效率数据 - 不缓存（个人敏感数据）
     */
    public OperationalEfficiencyDTO getLatestEfficiencyByCourier(Integer courierId) {
        OperationalEfficiencyMetrics metrics = operationalEfficiencyMapper.findLatestByCourier(courierId);
        return metrics != null ? convertToDTO(metrics) : null;
    }

    // ==================== 统计分析操作 ====================

    /**
     * 获取城市效率趋势 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'trend:' + #city + ':' + #startDate", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCityEfficiencyTrend(String city, LocalDate startDate) {
        log.info("查询数据库获取城市效率趋势[city={}]", city);
        return operationalEfficiencyMapper.getCityEfficiencyTrend(city, startDate);
    }

    /**
     * 获取配送员效率排行 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'courier_ranking:' + #city + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCourierEfficiencyRanking(String city, LocalDate startDate, int limit) {
        log.info("查询数据库获取配送员效率排行[city={}]", city);
        return operationalEfficiencyMapper.getCourierEfficiencyRanking(city, startDate, limit);
    }

    /**
     * 获取区域效率排行 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'region_ranking:' + #city + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getRegionEfficiencyRanking(String city, LocalDate startDate, int limit) {
        log.info("查询数据库获取区域效率排行[city={}]", city);
        return operationalEfficiencyMapper.getRegionEfficiencyRanking(city, startDate, limit);
    }

    /**
     * 获取效率分布统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'distribution:' + #city + ':' + #startDate", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getEfficiencyDistribution(String city, LocalDate startDate) {
        log.info("查询数据库获取效率分布统计[city={}]", city);
        return operationalEfficiencyMapper.getEfficiencyDistribution(city, startDate);
    }

    /**
     * 获取运营效率汇总统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'summary:' + #city + ':' + #startDate", unless = "#result == null")
    public Map<String, Object> getEfficiencySummary(String city, LocalDate startDate) {
        log.info("查询数据库获取效率汇总统计[city={}]", city);
        return operationalEfficiencyMapper.getEfficiencySummary(city, startDate);
    }

    /**
     * 获取城市间效率对比 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'comparison:' + #cities.toString() + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCityEfficiencyComparison(List<String> cities, LocalDate startDate, LocalDate endDate) {
        log.info("查询数据库获取城市效率对比，城市数: {}", cities.size());
        return operationalEfficiencyMapper.getCityEfficiencyComparison(cities, startDate, endDate);
    }

    /**
     * 统计城市效率记录数 - 不缓存（简单计数查询）
     */
    public int countByCity(String city) {
        return operationalEfficiencyMapper.countByCity(city);
    }

    /**
     * 统计配送员记录数 - 不缓存（简单计数查询）
     */
    public int countByCourier(Integer courierId) {
        return operationalEfficiencyMapper.countByCourier(courierId);
    }

    // ==================== 数据维护 ====================

    /**
     * 清理旧数据 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"efficiency", "stats"}, allEntries = true)
    public int cleanupOldData(LocalDate cutoffDate) {
        int result = operationalEfficiencyMapper.cleanupOldMetrics(cutoffDate);
        if (result > 0) {
            log.info("清理旧运营效率数据成功，删除 {} 条记录，已清除缓存", result);
        }
        return result;
    }

    // ==================== 私有方法 ====================

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