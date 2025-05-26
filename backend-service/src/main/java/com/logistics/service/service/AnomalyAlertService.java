package com.logistics.service.service;

import com.logistics.service.dao.entity.AnomalyAlert;
import com.logistics.service.dao.mapper.AnomalyAlertMapper;
import com.logistics.service.dto.AnomalyAlertDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AnomalyAlertService {

    @Autowired
    private AnomalyAlertMapper anomalyAlertMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
            List<AnomalyAlert> alerts = anomalyAlertMapper.findUnresolvedByCity(city);

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

            List<AnomalyAlert> alerts = anomalyAlertMapper.findBySeverity(severity);
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
            List<AnomalyAlert> alerts = anomalyAlertMapper.findByAnomalyType(anomalyType);
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

            List<AnomalyAlert> alerts = anomalyAlertMapper.findByCityAndDateRange(city, startDate, endDate);
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
            List<AnomalyAlert> alerts = anomalyAlertMapper.findByCourierAndDateRange(courierId, startDate, endDate);
            return alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按配送员获取告警失败", e);
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
            List<AnomalyAlert> alerts = anomalyAlertMapper.findHighRiskAlertsByCity(city);
            return alerts.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取高风险告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 解决告警
     */
    public boolean resolveAlert(Long alertId) {
        try {
            int updated = anomalyAlertMapper.resolveAlert(alertId, LocalDateTime.now());
            if (updated > 0) {
                // 清除相关缓存
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
    public int resolveAlertsBatch(List<Long> alertIds) {
        try {
            int totalResolved = 0;
            LocalDateTime resolvedTime = LocalDateTime.now();

            for (Long alertId : alertIds) {
                int updated = anomalyAlertMapper.resolveAlert(alertId, resolvedTime);
                if (updated > 0) {
                    totalResolved++;
                }
            }

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

    /**
     * 获取告警统计信息
     */
    public String getAlertStatistics(String city) {
        try {
            List<AnomalyAlert> allAlerts = anomalyAlertMapper.findByCityAndDateRange(city,
                    LocalDate.now().minusDays(7), LocalDate.now());

            long totalAlerts = allAlerts.size();
            long unresolvedAlerts = allAlerts.stream()
                    .filter(alert -> alert.getIsResolved() == null || !alert.getIsResolved())
                    .count();
            long highRiskAlerts = allAlerts.stream()
                    .filter(alert -> "HIGH".equals(alert.getAnomalySeverity()))
                    .count();
            long resolvedAlerts = totalAlerts - unresolvedAlerts;

            double resolutionRate = totalAlerts > 0 ? (double) resolvedAlerts / totalAlerts * 100 : 0;

            return String.format("总告警数: %d, 未解决: %d, 高风险: %d, 解决率: %.1f%%",
                    totalAlerts, unresolvedAlerts, highRiskAlerts, resolutionRate);
        } catch (Exception e) {
            log.error("获取告警统计失败", e);
            return "统计信息获取失败";
        }
    }

    /**
     * 清除告警相关缓存
     */
    private void clearAlertsCache() {
        try {
            // 使用通配符删除所有告警相关缓存
            redisTemplate.delete(redisTemplate.keys("alerts:*"));
            log.info("🗑️ 已清除告警相关缓存");
        } catch (Exception e) {
            log.warn("清除告警缓存失败", e);
        }
    }

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