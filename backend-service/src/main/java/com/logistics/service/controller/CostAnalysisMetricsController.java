package com.logistics.service.controller;

import com.logistics.service.dao.entity.CostAnalysisMetrics;
import com.logistics.service.service.CostAnalysisMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cost-analysis")
@CrossOrigin
public class CostAnalysisMetricsController {

    @Autowired
    private CostAnalysisMetricsService costAnalysisMetricsService;

    /**
     * 保存成本分析数据
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveCostAnalysis(@RequestBody CostAnalysisMetrics metrics) {
        Map<String, Object> response = new HashMap<>();
        try {
            int result = costAnalysisMetricsService.saveCostAnalysis(metrics);
            response.put("success", true);
            response.put("message", "成本分析数据保存成功");
            response.put("affectedRows", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量保存成本分析数据
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchSaveCostAnalysis(@RequestBody List<CostAnalysisMetrics> metricsList) {
        Map<String, Object> response = new HashMap<>();
        try {
            int result = costAnalysisMetricsService.batchSaveCostAnalysis(metricsList);
            response.put("success", true);
            response.put("message", "批量保存成功");
            response.put("affectedRows", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量保存失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据城市查询成本分析
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<Map<String, Object>> getCostAnalysisByCity(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<CostAnalysisMetrics> data = costAnalysisMetricsService.getCostAnalysisByCity(city, startDate, endDate);
            response.put("success", true);
            response.put("data", data);
            response.put("count", data.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据区域查询成本分析
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<Map<String, Object>> getCostAnalysisByRegion(
            @PathVariable Integer regionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<CostAnalysisMetrics> data = costAnalysisMetricsService.getCostAnalysisByRegion(regionId, startDate, endDate);
            response.put("success", true);
            response.put("data", data);
            response.put("count", data.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 多条件查询成本分析
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCostAnalysis(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer regionId,
            @RequestParam(required = false) String analysisType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<CostAnalysisMetrics> data = costAnalysisMetricsService.getCostAnalysisByConditions(
                    city, regionId, analysisType, startDate, endDate);
            response.put("success", true);
            response.put("data", data);
            response.put("count", data.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取城市成本趋势
     */
    @GetMapping("/trend/city/{city}")
    public ResponseEntity<Map<String, Object>> getCityCostTrend(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> data = costAnalysisMetricsService.getCityCostTrend(city, startDate);
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询成本趋势失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取区域成本排行
     */
    @GetMapping("/ranking/region")
    public ResponseEntity<Map<String, Object>> getRegionCostRanking(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> data = costAnalysisMetricsService.getRegionCostRanking(city, startDate, limit);
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询区域成本排行失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取分析类型统计
     */
    @GetMapping("/stats/analysis-type")
    public ResponseEntity<Map<String, Object>> getAnalysisTypeStats(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> data = costAnalysisMetricsService.getAnalysisTypeStats(city, startDate);
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询分析类型统计失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取高成本告警
     */
    @GetMapping("/alerts/high-cost")
    public ResponseEntity<Map<String, Object>> getHighCostAlerts(
            @RequestParam double threshold,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "10") int limit) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<CostAnalysisMetrics> data = costAnalysisMetricsService.getHighCostAlerts(threshold, date, limit);
            response.put("success", true);
            response.put("data", data);
            response.put("count", data.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询高成本告警失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取成本汇总统计
     */
    @GetMapping("/summary/{city}")
    public ResponseEntity<Map<String, Object>> getCostSummary(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = costAnalysisMetricsService.getCostSummary(city, startDate);
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询成本汇总失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新成本分析数据
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateCostAnalysis(@RequestBody CostAnalysisMetrics metrics) {
        Map<String, Object> response = new HashMap<>();
        try {
            int result = costAnalysisMetricsService.updateCostAnalysis(metrics);
            response.put("success", true);
            response.put("message", "更新成功");
            response.put("affectedRows", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 清理旧数据
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {

        Map<String, Object> response = new HashMap<>();
        try {
            int result = costAnalysisMetricsService.cleanupOldData(cutoffDate);
            response.put("success", true);
            response.put("message", "清理完成");
            response.put("deletedRows", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "清理失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 统计城市成本分析数量
     */
    @GetMapping("/count/{city}")
    public ResponseEntity<Map<String, Object>> countByCity(@PathVariable String city) {
        Map<String, Object> response = new HashMap<>();
        try {
            int count = costAnalysisMetricsService.countByCity(city);
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "统计失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}