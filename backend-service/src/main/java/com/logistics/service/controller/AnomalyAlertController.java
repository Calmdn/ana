package com.logistics.service.controller;

import com.logistics.service.dto.AnomalyAlertDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.AnomalyAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AnomalyAlertController {

    @Autowired
    private AnomalyAlertService anomalyAlertService;

    // ==================== 基础CRUD接口 ====================

    /**
     * 根据ID获取告警详情
     */
    @GetMapping("/{id}")
    public SimpleResponse<AnomalyAlertDTO> getAlertById(@PathVariable Long id) {
        try {
            log.info("请求获取告警详情，ID: {}", id);
            AnomalyAlertDTO alert = anomalyAlertService.getAlertById(id);
            if (alert != null) {
                return SimpleResponse.success(alert);
            } else {
                return SimpleResponse.error("告警不存在");
            }
        } catch (Exception e) {
            log.error("获取告警详情失败", e);
            return SimpleResponse.error("获取告警详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新告警描述
     */
    @PutMapping("/{id}/description")
    public SimpleResponse<String> updateAlertDescription(@PathVariable Long id, @RequestBody String description) {
        try {
            log.info("请求更新告警描述，ID: {}", id);
            boolean success = anomalyAlertService.updateAlertDescription(id, description);
            if (success) {
                return SimpleResponse.success("告警描述更新成功");
            } else {
                return SimpleResponse.error("更新告警描述失败");
            }
        } catch (Exception e) {
            log.error("更新告警描述失败", e);
            return SimpleResponse.error("更新告警描述失败: " + e.getMessage());
        }
    }

    /**
     * 更新告警严重程度
     */
    @PutMapping("/{id}/severity")
    public SimpleResponse<String> updateAlertSeverity(@PathVariable Long id, @RequestBody String severity) {
        try {
            log.info("请求更新告警严重程度，ID: {}, 新严重程度: {}", id, severity);
            boolean success = anomalyAlertService.updateAlertSeverity(id, severity);
            if (success) {
                return SimpleResponse.success("告警严重程度更新成功");
            } else {
                return SimpleResponse.error("更新告警严重程度失败");
            }
        } catch (Exception e) {
            log.error("更新告警严重程度失败", e);
            return SimpleResponse.error("更新告警严重程度失败: " + e.getMessage());
        }
    }

    // ==================== 原有查询接口 ====================

    /**
     * 获取指定城市的未解决告警
     */
    @GetMapping("/unresolved/{city}")
    public SimpleResponse<List<AnomalyAlertDTO>> getUnresolvedAlerts(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的未解决告警", city);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getUnresolvedAlertsByCity(city);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("获取未解决告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    /**
     * 按严重程度获取告警
     */
    @GetMapping("/severity/{severity}")
    public SimpleResponse<List<AnomalyAlertDTO>> getAlertsBySeverity(@PathVariable String severity) {
        try {
            log.info("请求严重程度 {} 的告警", severity);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getAlertsBySeverity(severity);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("按严重程度获取告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    /**
     * 按异常类型获取告警
     */
    @GetMapping("/type/{anomalyType}")
    public SimpleResponse<List<AnomalyAlertDTO>> getAlertsByType(@PathVariable String anomalyType) {
        try {
            log.info("请求异常类型 {} 的告警", anomalyType);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getAlertsByType(anomalyType);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("按异常类型获取告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    /**
     * 按时间范围获取告警
     */
    @GetMapping("/range/{city}")
    public SimpleResponse<List<AnomalyAlertDTO>> getAlertsByDateRange(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 时间范围 {} 到 {} 的告警", city, startDate, endDate);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getAlertsByDateRange(city, startDate, endDate);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("按时间范围获取告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    /**
     * 按配送员获取告警
     */
    @GetMapping("/courier/{courierId}")
    public SimpleResponse<List<AnomalyAlertDTO>> getAlertsByCourier(
            @PathVariable String courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求配送员 {} 时间范围 {} 到 {} 的告警", courierId, startDate, endDate);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getAlertsByCourier(courierId, startDate, endDate);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("按配送员获取告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日告警
     */
    @GetMapping("/today/{city}")
    public SimpleResponse<List<AnomalyAlertDTO>> getTodayAlerts(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的今日告警", city);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getTodayAlerts(city);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("获取今日告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    /**
     * 获取高风险告警
     */
    @GetMapping("/high-risk/{city}")
    public SimpleResponse<List<AnomalyAlertDTO>> getHighRiskAlerts(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的高风险告警", city);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getHighRiskAlerts(city);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("获取高风险告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    // ==================== 新增查询接口 ====================

    /**
     * 获取订单相关告警
     */
    @GetMapping("/order/{orderId}")
    public SimpleResponse<List<AnomalyAlertDTO>> getAlertsByOrder(@PathVariable String orderId) {
        try {
            log.info("请求订单 {} 的告警", orderId);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getAlertsByOrder(orderId);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("按订单获取告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近的告警
     */
    @GetMapping("/recent")
    public SimpleResponse<List<AnomalyAlertDTO>> getRecentAlerts(@RequestParam(defaultValue = "50") int limit) {
        try {
            log.info("请求最近 {} 条告警", limit);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getRecentAlerts(limit);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("获取最近告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    /**
     * 获取偏差最大的告警
     */
    @GetMapping("/highest-deviation")
    public SimpleResponse<List<AnomalyAlertDTO>> getHighestDeviationAlerts(@RequestParam(defaultValue = "20") int limit) {
        try {
            log.info("请求偏差最大的 {} 条告警", limit);
            List<AnomalyAlertDTO> alerts = anomalyAlertService.getHighestDeviationAlerts(limit);
            return SimpleResponse.success(alerts);
        } catch (Exception e) {
            log.error("获取偏差最大告警失败", e);
            return SimpleResponse.error("获取告警失败: " + e.getMessage());
        }
    }

    // ==================== 解决告警接口 ====================

    /**
     * 解决告警
     */
    @PutMapping("/resolve/{alertId}")
    public SimpleResponse<String> resolveAlert(@PathVariable Long alertId) {
        try {
            log.info("请求解决告警 ID: {}", alertId);
            boolean success = anomalyAlertService.resolveAlert(alertId);
            if (success) {
                return SimpleResponse.success("告警已成功解决");
            } else {
                return SimpleResponse.error("解决告警失败，请检查告警ID");
            }
        } catch (Exception e) {
            log.error("解决告警失败", e);
            return SimpleResponse.error("解决告警失败: " + e.getMessage());
        }
    }

    /**
     * 批量解决告警
     */
    @PutMapping("/resolve/batch")
    public SimpleResponse<String> resolveAlertsBatch(@RequestBody List<Long> alertIds) {
        try {
            log.info("请求批量解决告警，数量: {}", alertIds.size());
            int resolvedCount = anomalyAlertService.resolveAlertsBatch(alertIds);
            if (resolvedCount > 0) {
                return SimpleResponse.success(String.format("成功解决 %d 个告警", resolvedCount));
            } else {
                return SimpleResponse.error("批量解决告警失败");
            }
        } catch (Exception e) {
            log.error("批量解决告警失败", e);
            return SimpleResponse.error("批量解决告警失败: " + e.getMessage());
        }
    }

    // ==================== 统计分析接口 ====================

    /**
     * 获取告警数量统计
     */
    @GetMapping("/count")
    public SimpleResponse<Integer> getAlertsCount(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String anomalyType,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Boolean isResolved) {
        try {
            log.info("请求告警数量统计");
            int count = anomalyAlertService.getAlertsCount(city, anomalyType, severity, startDate, endDate, isResolved);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("获取告警数量统计失败", e);
            return SimpleResponse.error("获取统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取未解决告警数量
     */
    @GetMapping("/count/unresolved")
    public SimpleResponse<Integer> getUnresolvedAlertsCount(@RequestParam(required = false) String city) {
        try {
            log.info("请求未解决告警数量，城市: {}", city);
            int count = anomalyAlertService.getUnresolvedAlertsCount(city);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("获取未解决告警数量失败", e);
            return SimpleResponse.error("获取统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取告警统计分析
     */
    @GetMapping("/stats")
    public SimpleResponse<List<Map<String, Object>>> getAlertStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "type_severity") String groupBy) {
        try {
            log.info("请求告警统计分析，时间范围: {} 到 {}, 分组方式: {}", startDate, endDate, groupBy);
            List<Map<String, Object>> stats = anomalyAlertService.getAlertStats(startDate, endDate, groupBy);
            return SimpleResponse.success(stats);
        } catch (Exception e) {
            log.error("获取告警统计分析失败", e);
            return SimpleResponse.error("获取统计分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日告警统计
     */
    @GetMapping("/stats/today")
    public SimpleResponse<List<Map<String, Object>>> getTodayAlertStats() {
        try {
            log.info("请求今日告警统计");
            List<Map<String, Object>> stats = anomalyAlertService.getTodayAlertStats();
            return SimpleResponse.success(stats);
        } catch (Exception e) {
            log.error("获取今日告警统计失败", e);
            return SimpleResponse.error("获取统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市告警趋势
     */
    @GetMapping("/trend/{city}")
    public SimpleResponse<List<Map<String, Object>>> getCityAlertTrend(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 告警趋势，时间范围: {} 到 {}", city, startDate, endDate);
            List<Map<String, Object>> trend = anomalyAlertService.getCityAlertTrend(city, startDate, endDate);
            return SimpleResponse.success(trend);
        } catch (Exception e) {
            log.error("获取城市告警趋势失败", e);
            return SimpleResponse.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取小时级告警分布
     */
    @GetMapping("/distribution/hourly/{city}")
    public SimpleResponse<List<Map<String, Object>>> getHourlyAlertDistribution(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            log.info("请求城市 {} 日期 {} 的小时级告警分布", city, targetDate);
            List<Map<String, Object>> distribution = anomalyAlertService.getHourlyAlertDistribution(city, targetDate);
            return SimpleResponse.success(distribution);
        } catch (Exception e) {
            log.error("获取小时级告警分布失败", e);
            return SimpleResponse.error("获取分布数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送员告警排行
     */
    @GetMapping("/ranking/courier/{city}")
    public SimpleResponse<List<Map<String, Object>>> getCourierAlertRanking(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求城市 {} 配送员告警排行，起始日期: {}, 限制: {}", city, startDate, limit);
            List<Map<String, Object>> ranking = anomalyAlertService.getCourierAlertRanking(city, startDate, limit);
            return SimpleResponse.success(ranking);
        } catch (Exception e) {
            log.error("获取配送员告警排行失败", e);
            return SimpleResponse.error("获取排行数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取告警统计信息（综合统计）
     */
    @GetMapping("/statistics/{city}")
    public SimpleResponse<String> getAlertStatistics(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的告警统计信息", city);
            String statistics = anomalyAlertService.getAlertStatistics(city);
            return SimpleResponse.success(statistics);
        } catch (Exception e) {
            log.error("获取告警统计失败", e);
            return SimpleResponse.error("获取统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 数据维护接口 ====================

    /**
     * 清理旧告警数据
     */
    @DeleteMapping("/cleanup")
    public SimpleResponse<String> cleanupOldAlerts(@RequestParam(defaultValue = "90") int daysToKeep) {
        try {
            log.info("请求清理 {} 天前的旧告警数据", daysToKeep);
            int deleted = anomalyAlertService.cleanupOldAlerts(daysToKeep);
            return SimpleResponse.success(String.format("成功清理 %d 条旧告警数据", deleted));
        } catch (Exception e) {
            log.error("清理旧告警数据失败", e);
            return SimpleResponse.error("清理数据失败: " + e.getMessage());
        }
    }
}