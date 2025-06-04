package com.logistics.service.service;

import com.logistics.service.dao.entity.AnomalyAlert;
import com.logistics.service.dao.mapper.AnomalyAlertMapper;
import com.logistics.service.dto.AnomalyAlertDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnomalyAlertService {

    @Autowired
    private AnomalyAlertMapper anomalyAlertMapper;

    // ==================== 基础CRUD操作 ====================

    /**
     * 根据ID获取告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'alert:' + #id", unless = "#result == null")
    public AnomalyAlertDTO getAlertById(Long id) {
        try {
            log.info("查询数据库获取告警[id={}]", id);
            AnomalyAlert alert = anomalyAlertMapper.findById(id);
            return alert != null ? convertToDTO(alert) : null;
        } catch (Exception e) {
            log.error("根据ID获取告警失败", e);
            return null;
        }
    }

    // ==================== 查询操作 ====================

    /**
     * 获取指定城市未解决的异常告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'unresolved:' + #city", unless = "#result.isEmpty()")
    public List<AnomalyAlertDTO> getUnresolvedAlertsByCity(String city) {
        try {
            log.info("查询数据库获取未解决告警[city={}]", city);
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    city, null, null, null, null, null, null, false, null);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取异常告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据严重程度获取告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'severity:' + #severity", unless = "#result.isEmpty()")
    public List<AnomalyAlertDTO> getAlertsBySeverity(String severity) {
        try {
            log.info("查询数据库获取严重程度告警[severity={}]", severity);
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    null, null, severity, null, null, null, null, null, null);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按严重程度获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据异常类型获取告警 - 不缓存（变化频繁）
     */
    public List<AnomalyAlertDTO> getAlertsByType(String anomalyType) {
        try {
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    null, anomalyType, null, null, null, null, null, null, null);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按异常类型获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定时间范围的告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'range:' + #city + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<AnomalyAlertDTO> getAlertsByDateRange(String city, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("查询数据库获取时间范围告警[city={}]", city);
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    city, null, null, null, null, startDate, endDate, null, null);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按时间范围获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定配送员的告警 - 不缓存（敏感数据）
     */
    public List<AnomalyAlertDTO> getAlertsByCourier(String courierId, LocalDate startDate, LocalDate endDate) {
        try {
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    null, null, null, courierId, null, startDate, endDate, null, null);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按配送员获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取订单告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'order:' + #orderId", unless = "#result.isEmpty()")
    public List<AnomalyAlertDTO> getAlertsByOrder(String orderId) {
        try {
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    null, null, null, null, orderId, null, null, null, null);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按订单获取告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'today:' + #city", unless = "#result.isEmpty()")
    public List<AnomalyAlertDTO> getTodayAlerts(String city) {
        LocalDate today = LocalDate.now();
        return getAlertsByDateRange(city, today, today);
    }

    /**
     * 获取高风险告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'high_risk:' + #city", unless = "#result.isEmpty()")
    public List<AnomalyAlertDTO> getHighRiskAlerts(String city) {
        try {
            List<AnomalyAlert> alerts = anomalyAlertMapper.findAlerts(
                    city, null, "HIGH", null, null, null, null, false, null);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取高风险告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查找异常值超过阈值最多的告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'highest_deviation:' + #limit", unless = "#result.isEmpty()")
    public List<AnomalyAlertDTO> getHighestDeviationAlerts(int limit) {
        try {
            log.info("查询数据库获取最高偏差告警");
            List<AnomalyAlert> alerts = anomalyAlertMapper.findHighestDeviationAlerts(limit);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最高偏差告警失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最近的告警 - 添加缓存
     */
    @Cacheable(value = "alerts", key = "'recent:' + #limit", unless = "#result.isEmpty()")
    public List<AnomalyAlertDTO> getRecentAlerts(int limit) {
        try {
            log.info("查询数据库获取最近告警");
            List<AnomalyAlert> alerts = anomalyAlertMapper.findRecentAlerts(limit);
            return alerts.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最近告警失败", e);
            return new ArrayList<>();
        }
    }

    // ==================== 解决告警操作 ====================

    /**
     * 解决告警 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = "alerts", allEntries = true)
    public boolean resolveAlert(Long alertId) {
        try {
            int updated = anomalyAlertMapper.resolveAlert(alertId, LocalDateTime.now());
            if (updated > 0) {
                log.info("告警已解决，ID={}，已清除缓存", alertId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("解决告警失败", e);
            return false;
        }
    }

    /**
     * 批量解决告警 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = "alerts", allEntries = true)
    public int resolveAlertsBatch(List<Long> alertIds) {
        try {
            if (alertIds == null || alertIds.isEmpty()) {
                return 0;
            }

            LocalDateTime resolvedTime = LocalDateTime.now();
            int totalResolved = anomalyAlertMapper.resolveAlertsBatch(alertIds, resolvedTime);

            if (totalResolved > 0) {
                log.info("批量解决告警完成，共解决 {} 个，已清除缓存", totalResolved);
            }

            return totalResolved;
        } catch (Exception e) {
            log.error("批量解决告警失败", e);
            return 0;
        }
    }

    // ==================== 统计操作 ====================

    /**
     * 获取告警数量统计 - 不缓存（查询快）
     */
    public int getAlertsCount(String city, String anomalyType, String severity,
                              LocalDate startDate, LocalDate endDate, Boolean isResolved) {
        try {
            return anomalyAlertMapper.countAlerts(city, anomalyType, severity, null, null,
                    startDate, endDate, isResolved);
        } catch (Exception e) {
            log.error("获取告警数量统计失败", e);
            return 0;
        }
    }

    public int getUnresolvedAlertsCount(String city) {
        return getAlertsCount(city, null, null, null, null, false);
    }

    /**
     * 获取告警统计分析 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'alert_stats:' + #startDate + ':' + #endDate + ':' + #groupBy",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getAlertStats(LocalDate startDate, LocalDate endDate, String groupBy) {
        try {
            log.info("查询数据库获取告警统计分析");
            return anomalyAlertMapper.getAlertStats(startDate, endDate, groupBy);
        } catch (Exception e) {
            log.error("获取告警统计分析失败", e);
            return new ArrayList<>();
        }
    }

    @Cacheable(value = "stats", key = "'today_stats'", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getTodayAlertStats() {
        LocalDate today = LocalDate.now();
        return getAlertStats(today, today, "type_severity");
    }

    /**
     * 获取城市告警趋势 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'trend:' + #city + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCityAlertTrend(String city, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("查询数据库获取城市告警趋势");
            return anomalyAlertMapper.getCityAlertTrend(city, startDate, endDate);
        } catch (Exception e) {
            log.error("获取城市告警趋势失败", e);
            return new ArrayList<>();
        }
    }

    // 其他统计方法保持不变，不加缓存（实时性要求或查询频率低）
    public List<Map<String, Object>> getHourlyAlertDistribution(String city, LocalDate date) {
        try {
            return anomalyAlertMapper.getHourlyAlertDistribution(city, date);
        } catch (Exception e) {
            log.error("获取小时级告警分布失败", e);
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getCourierAlertRanking(String city, LocalDate startDate, int limit) {
        try {
            return anomalyAlertMapper.getCourierAlertRanking(city, startDate, limit);
        } catch (Exception e) {
            log.error("获取配送员告警排行失败", e);
            return new ArrayList<>();
        }
    }

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
     * 更新告警描述 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = "alerts", allEntries = true)
    public boolean updateAlertDescription(Long id, String description) {
        try {
            int updated = anomalyAlertMapper.updateAlertDescription(id, description);
            if (updated > 0) {
                log.info("更新告警描述成功，ID={}，已清除缓存", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新告警描述失败", e);
            return false;
        }
    }

    /**
     * 更新告警严重程度 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = "alerts", allEntries = true)
    public boolean updateAlertSeverity(Long id, String severity) {
        try {
            int updated = anomalyAlertMapper.updateAlertSeverity(id, severity);
            if (updated > 0) {
                log.info("更新告警严重程度成功，ID={}，已清除缓存", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新告警严重程度失败", e);
            return false;
        }
    }

    /**
     * 清理旧告警数据 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"alerts", "stats"}, allEntries = true)
    public int cleanupOldAlerts(int daysToKeep) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            int deleted = anomalyAlertMapper.cleanupOldAlerts(cutoffDate);

            if (deleted > 0) {
                log.info("清理旧告警数据成功，删除 {} 条记录，已清除缓存", deleted);
            }

            return deleted;
        } catch (Exception e) {
            log.error("清理旧告警数据失败", e);
            return 0;
        }
    }

    // ==================== 私有方法 ====================

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