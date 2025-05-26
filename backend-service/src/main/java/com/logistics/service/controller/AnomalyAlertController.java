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

@Slf4j
@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AnomalyAlertController {

    @Autowired
    private AnomalyAlertService anomalyAlertService;

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

    /**
     * 获取告警统计信息
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
}