package com.logistics.service.service;

import com.logistics.service.dao.entity.AnomalyAlert;
import com.logistics.service.dao.mapper.AnomalyAlertMapper;
import com.logistics.service.dto.AnomalyAlertDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AnomalyAlertService {

    @Autowired
    private AnomalyAlertMapper anomalyAlertMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== 基础CRUD操作 ====================

    /**
     * 根据ID获取告警
     */
    public AnomalyAlertDTO getAlertById(Long id) {
        try {
            String key = "alert:id:" + id;

            @SuppressWarnings("unchecked")
            AnomalyAlertDTO cache = (AnomalyAlertDTO) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取告警[id={}]", id);
                return cache;
            }

            AnomalyAlert alert = anomalyAlertMapper.findById(id);
            if (alert != null) {
                AnomalyAlertDTO dto = convertToDTO(alert);
                redisTemplate.opsForValue().set(key, dto, 30, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存告警[id={}]，ttl=30m", id);
                return dto;
            }
            return null;
        } catch (Exception e) {
            log.error("根据ID获取告警失败", e);
            return null;
        }
    }

    // ==================== 查询操作（使用新的动态查询方法） ====================

    /**
     * 获取指定城市未解决的异常告警
     */
    public List<AnomalyAlertDTO> getUnresolvedAlertsByCity(String city) {
        try {
            String key = "alerts:unresolved:" + city;

            @SuppressWarnings("unchecked")
            List<AnomalyAlertDTO> cache = (List<AnomalyAlertDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取未解决告警[city={}], size={}", city, cache.size());
                return cache;
            }

            log.info("🔍 Redis 未命中，查询 MySQL 未解决告警[city={}]", city);
            // 使用新的动态查询方法
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    city, null, null, null, null, null, null, false, null);

            List<AnomalyAlertDTO> result = alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 15, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存未解决告警[city={}]，ttl=15m", city);
            }

            return result;
        } catch (Exception e) {
            log.error("获取异常告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据严重程度获取告警
     */
    public List<AnomalyAlertDTO> getAlertsBySeverity(String severity) {
        try {
            String key = "alerts:severity:" + severity;

            @SuppressWarnings("unchecked")
            List<AnomalyAlertDTO> cache = (List<AnomalyAlertDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取严重程度告警[severity={}], size={}", severity, cache.size());
                return cache;
            }

            // 使用新的动态查询方法
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    null, null, severity, null, null, null, null, null, null);

            List<AnomalyAlertDTO> result = alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 10, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存严重程度告警[severity={}]，ttl=10m", severity);
            }

            return result;
        } catch (Exception e) {
            log.error("按严重程度获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据异常类型获取告警
     */
    public List<AnomalyAlertDTO> getAlertsByType(String anomalyType) {
        try {
            // 使用新的动态查询方法
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    null, anomalyType, null, null, null, null, null, null, null);

            return alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按异常类型获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定时间范围的告警
     */
    public List<AnomalyAlertDTO> getAlertsByDateRange(String city, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("alerts:range:%s:%s:%s", city, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<AnomalyAlertDTO> cache = (List<AnomalyAlertDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取时间范围告警[city={}], size={}", city, cache.size());
                return cache;
            }

            // 使用新的动态查询方法
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    city, null, null, null, null, startDate, endDate, null, null);

            List<AnomalyAlertDTO> result = alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 30, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存时间范围告警[city={}]，ttl=30m", city);
            }

            return result;
        } catch (Exception e) {
            log.error("按时间范围获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定配送员的告警
     */
    public List<AnomalyAlertDTO> getAlertsByCourier(String courierId, LocalDate startDate, LocalDate endDate) {
        try {
            // 使用新的动态查询方法
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    null, null, null, courierId, null, startDate, endDate, null, null);

            return alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按配送员获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取订单告警
     */
    public List<AnomalyAlertDTO> getAlertsByOrder(String orderId) {
        try {
            // 使用新的动态查询方法
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    null, null, null, null, orderId, null, null, null, null);

            return alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按订单获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日告警
     */
    public List<AnomalyAlertDTO> getTodayAlerts(String city) {
        LocalDate today = LocalDate.now();
        return getAlertsByDateRange(city, today, today);
    }

    /**
     * 获取高风险告警
     */
    public List<AnomalyAlertDTO> getHighRiskAlerts(String city) {
        try {
            // 使用新的动态查询方法查询高风险未解决告警
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    city, null, "HIGH", null, null, null, null, false, null);

            return alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取高风险告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查找异常值超过阈值最多的告警
     */
    public List<AnomalyAlertDTO> getHighestDeviationAlerts(int limit) {
        try {
            String key = "alerts:highest_deviation:" + limit;

            @SuppressWarnings("unchecked")
            List<AnomalyAlertDTO> cache = (List<AnomalyAlertDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取最高偏差告警, size={}", cache.size());
                return cache;
            }

            List<AnomalyAlert> alerts = anomalyAlertMapper.findHighestDeviationAlerts(limit);
            List<AnomalyAlertDTO> result = alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 15, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存最高偏差告警，ttl=15m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取最高偏差告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最近的告警
     */
    public List<AnomalyAlertDTO> getRecentAlerts(int limit) {
        try {
            String key = "alerts:recent:" + limit;

            @SuppressWarnings("unchecked")
            List<AnomalyAlertDTO> cache = (List<AnomalyAlertDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取最近告警, size={}", cache.size());
                return cache;
            }

            List<AnomalyAlert> alerts = anomalyAlertMapper.findRecentAlerts(limit);
            List<AnomalyAlertDTO> result = alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 5, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存最近告警，ttl=5m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取最近告警失败", e);
            return new ArrayList<>();
        }
    }

    // ==================== 解决告警操作 ====================

    /**
     * 解决告警
     */
    @Transactional
    public boolean resolveAlert(Long alertId) {
        try {
            int updated = anomalyAlertMapper.resolveAlert(alertId, LocalDateTime.now());
            if (updated > 0) {
                clearAlertsCache();
                log.info("✅ 告警已解决，ID={}", alertId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("解决告警失败", e);
            return false;
        }
    }

    /**
     * 批量解决告警
     */
    @Transactional
    public int resolveAlertsBatch(List<Long> alertIds) {
        try {
            if (alertIds == null || alertIds.isEmpty()) {
                return 0;
            }

            LocalDateTime resolvedTime = LocalDateTime.now();
            int totalResolved = anomalyAlertMapper.resolveAlertsBatch(alertIds, resolvedTime);

            if (totalResolved > 0) {
                clearAlertsCache();
                log.info("✅ 批量解决告警完成，共解决 {} 个", totalResolved);
            }

            return totalResolved;
        } catch (Exception e) {
            log.error("批量解决告警失败", e);
            return 0;
        }
    }

    // ==================== 统计操作 ====================

    /**
     * 获取告警数量统计
     */
    public int getAlertsCount(String city, String anomalyType, String severity,
                              LocalDate startDate, LocalDate endDate, Boolean isResolved) {
        try {
            return anomalyAlertMapper.countAlerts(city, anomalyType, severity,null,null, startDate, endDate, isResolved);
        } catch (Exception e) {
            log.error("获取告警数量统计失败", e);
            return 0;
        }
    }

    /**
     * 获取未解决告警数量
     */
    public int getUnresolvedAlertsCount(String city) {
        return getAlertsCount(city, null, null, null, null, false);
    }

    /**
     * 获取告警统计分析
     */
    public List<Map<String, Object>> getAlertStats(LocalDate startDate, LocalDate endDate, String groupBy) {
        try {
            String key = String.format("stats:alerts:%s:%s:%s", startDate, endDate, groupBy);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cache = (List<Map<String, Object>>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取告警统计");
                return cache;
            }

            List<Map<String, Object>> result = anomalyAlertMapper.getAlertStats(startDate, endDate, groupBy);

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 60, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存告警统计，ttl=60m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取告警统计分析失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日告警统计
     */
    public List<Map<String, Object>> getTodayAlertStats() {
        LocalDate today = LocalDate.now();
        return getAlertStats(today, today, "type_severity");
    }

    /**
     * 获取城市告警趋势
     */
    public List<Map<String, Object>> getCityAlertTrend(String city, LocalDate startDate, LocalDate endDate) {
        try {
            String key = String.format("trend:city:%s:%s:%s", city, startDate, endDate);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cache = (List<Map<String, Object>>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取城市告警趋势");
                return cache;
            }

            List<Map<String, Object>> result = anomalyAlertMapper.getCityAlertTrend(city, startDate, endDate);

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 120, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存城市告警趋势，ttl=120m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取城市告警趋势失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取小时级告警分布
     */
    public List<Map<String, Object>> getHourlyAlertDistribution(String city, LocalDate date) {
        try {
            return anomalyAlertMapper.getHourlyAlertDistribution(city, date);
        } catch (Exception e) {
            log.error("获取小时级告警分布失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取配送员告警排行
     */
    public List<Map<String, Object>> getCourierAlertRanking(String city, LocalDate startDate, int limit) {
        try {
            return anomalyAlertMapper.getCourierAlertRanking(city, startDate, limit);
        } catch (Exception e) {
            log.error("获取配送员告警排行失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取告警统计信息（综合统计）
     */
    public String getAlertStatistics(String city) {
        try {
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();

            int totalAlerts = getAlertsCount(city, null, null, startDate, endDate, null);
            int unresolvedAlerts = getAlertsCount(city, null, null, startDate, endDate, false);
            int highRiskAlerts = getAlertsCount(city, null, "HIGH", startDate, endDate, null);
            int resolvedAlerts = totalAlerts - unresolvedAlerts;

            double resolutionRate = totalAlerts > 0 ? (double) resolvedAlerts / totalAlerts * 100 : 0;

            return String.format("总告警数: %d, 未解决: %d, 高风险: %d, 解决率: %.1f%%",
                    totalAlerts, unresolvedAlerts, highRiskAlerts, resolutionRate);
        } catch (Exception e) {
            log.error("获取告警统计失败", e);
            return "统计信息获取失败";
        }
    }

    // ==================== 数据维护 ====================

    /**
     * 更新告警描述
     */
    @Transactional
    public boolean updateAlertDescription(Long id, String description) {
        try {
            int updated = anomalyAlertMapper.updateAlertDescription(id, description);
            if (updated > 0) {
                clearAlertsCache();
                log.info("✅ 更新告警描述成功，ID={}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新告警描述失败", e);
            return false;
        }
    }

    /**
     * 更新告警严重程度
     */
    @Transactional
    public boolean updateAlertSeverity(Long id, String severity) {
        try {
            int updated = anomalyAlertMapper.updateAlertSeverity(id, severity);
            if (updated > 0) {
                clearAlertsCache();
                log.info("✅ 更新告警严重程度成功，ID={}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新告警严重程度失败", e);
            return false;
        }
    }

    /**
     * 清理旧告警数据
     */
    @Transactional
    public int cleanupOldAlerts(int daysToKeep) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            int deleted = anomalyAlertMapper.cleanupOldAlerts(cutoffDate);

            if (deleted > 0) {
                clearAlertsCache();
                log.info("✅ 清理旧告警数据成功，删除 {} 条记录", deleted);
            }

            return deleted;
        } catch (Exception e) {
            log.error("清理旧告警数据失败", e);
            return 0;
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 清除告警相关缓存
     */
    private void clearAlertsCache() {
        try {
            // 使用通配符删除所有告警相关缓存
            redisTemplate.delete(redisTemplate.keys("alerts:*"));
            redisTemplate.delete(redisTemplate.keys("alert:*"));
            redisTemplate.delete(redisTemplate.keys("stats:*"));
            redisTemplate.delete(redisTemplate.keys("trend:*"));
            log.info("🗑️ 已清除告警相关缓存");
        } catch (Exception e) {
            log.warn("清除告警缓存失败", e);
        }
    }

    /**
     * 实体转DTO
     */
    private AnomalyAlertDTO convertToDTO(AnomalyAlert alert) {
        AnomalyAlertDTO dto = new AnomalyAlertDTO();
        dto.setId(alert.getId());
        dto.setAnomalyType(alert.getAnomalyType());
        dto.setCity(alert.getCity());
        dto.setOrderId(alert.getOrderId());
        dto.setCourierId(alert.getCourierId());
        dto.setAnomalySeverity(alert.getAnomalySeverity());
        dto.setAnomalyValue(alert.getAnomalyValue());
        dto.setThresholdValue(alert.getThresholdValue());
        dto.setDescription(alert.getDescription());
        dto.setOriginalTime(alert.getOriginalTime());
        dto.setAnalysisDate(alert.getAnalysisDate());
        dto.setAnalysisHour(alert.getAnalysisHour());
        dto.setIsResolved(alert.getIsResolved());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setResolvedAt(alert.getResolvedAt());
        return dto;
    }
}