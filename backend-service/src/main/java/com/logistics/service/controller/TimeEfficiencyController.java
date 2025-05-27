package com.logistics.service.controller;

import com.logistics.service.dao.entity.TimeEfficiencyMetrics;
import com.logistics.service.dto.TimeEfficiencyDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.TimeEfficiencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/time-efficiency")
@CrossOrigin(origins = "*")
public class TimeEfficiencyController {

    @Autowired
    private TimeEfficiencyService timeEfficiencyService;

    /**
     * 保存时间效率数据
     */
    @PostMapping
    public SimpleResponse<String> saveTimeEfficiency(@RequestBody TimeEfficiencyMetrics metrics) {
        try {
            log.info("保存时间效率数据: city={}, date={}, totalDeliveries={}",
                    metrics.getCity(), metrics.getDate(), metrics.getTotalDeliveries());
            int result = timeEfficiencyService.saveTimeEfficiency(metrics);
            return SimpleResponse.success("保存成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("保存时间效率数据失败", e);
            return SimpleResponse.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存时间效率数据
     */
    @PostMapping("/batch")
    public SimpleResponse<String> batchSaveTimeEfficiency(@RequestBody List<TimeEfficiencyMetrics> metricsList) {
        try {
            log.info("批量保存时间效率数据，数量: {}", metricsList.size());
            int result = timeEfficiencyService.batchSaveTimeEfficiency(metricsList);
            return SimpleResponse.success("批量保存成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("批量保存时间效率数据失败", e);
            return SimpleResponse.error("批量保存失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日时间效率
     */
    @GetMapping("/today/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getTodayTimeEfficiency(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的今日时间效率", city);
            List<TimeEfficiencyDTO> data = timeEfficiencyService.getTodayTimeEfficiency(city);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取今日时间效率失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定日期的时间效率
     */
    @GetMapping("/date/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getTimeEfficiencyByDate(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的时间效率", city, date);
            List<TimeEfficiencyDTO> data = timeEfficiencyService.getTimeEfficiencyByDate(city, date);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取指定日期时间效率失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取效率趋势分析
     */
    @GetMapping("/trend/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getDeliveryEfficiencyTrend(
            @PathVariable String city,
            @RequestParam(defaultValue = "7") int days) {
        try {
            log.info("请求城市 {} 最近 {} 天的效率趋势", city, days);
            List<TimeEfficiencyDTO> trend = timeEfficiencyService.getDeliveryEfficiencyTrend(city, days);
            return SimpleResponse.success(trend);
        } catch (Exception e) {
            log.error("获取效率趋势失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取效率趋势统计
     */
    @GetMapping("/trend-stats/{city}")
    public SimpleResponse<List<Map<String, Object>>> getDeliveryEfficiencyTrendStats(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的效率趋势统计", city, startDate);
            List<Map<String, Object>> data = timeEfficiencyService.getDeliveryEfficiencyTrendStats(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取效率趋势统计失败", e);
            return SimpleResponse.error("获取趋势统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定时间范围的时间效率
     */
    @GetMapping("/range/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getTimeEfficiencyByRange(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 时间范围 {} 到 {} 的时间效率", city, startDate, endDate);
            List<TimeEfficiencyDTO> data = timeEfficiencyService.getTimeEfficiencyByCity(city, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取时间效率失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 多条件搜索时间效率
     */
    @GetMapping("/search")
    public SimpleResponse<List<TimeEfficiencyDTO>> searchTimeEfficiency(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Double minFastRate,
            @RequestParam(required = false) Double maxSlowRate) {
        try {
            log.info("多条件搜索时间效率: city={}, startDate={}, endDate={}, minFastRate={}, maxSlowRate={}",
                    city, startDate, endDate, minFastRate, maxSlowRate);
            List<TimeEfficiencyDTO> data = timeEfficiencyService.getTimeEfficiencyByConditions(
                    city, startDate, endDate, minFastRate, maxSlowRate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("多条件搜索时间效率失败", e);
            return SimpleResponse.error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取效率分布统计
     */
    @GetMapping("/distribution/{city}")
    public SimpleResponse<List<Map<String, Object>>> getEfficiencyDistribution(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的效率分布统计", city, startDate);
            List<Map<String, Object>> data = timeEfficiencyService.getEfficiencyDistribution(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取效率分布统计失败", e);
            return SimpleResponse.error("获取分布统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取时间效率排行
     */
    @GetMapping("/ranking")
    public SimpleResponse<List<Map<String, Object>>> getTimeEfficiencyRanking(
            @RequestParam List<String> cities,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求城市 {} 从 {} 开始的时间效率排行，限制 {} 条", cities, startDate, limit);
            List<Map<String, Object>> data = timeEfficiencyService.getTimeEfficiencyRanking(cities, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取时间效率排行失败", e);
            return SimpleResponse.error("获取排行失败: " + e.getMessage());
        }
    }

    /**
     * 获取慢配送分析
     */
    @GetMapping("/slow-delivery/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getSlowDeliveryAnalysis(
            @PathVariable String city,
            @RequestParam double threshold,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求城市 {} 慢配送分析，阈值: {}, 开始日期: {}, 限制: {}", city, threshold, startDate, limit);
            List<TimeEfficiencyDTO> data = timeEfficiencyService.getSlowDeliveryAnalysis(city, threshold, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取慢配送分析失败", e);
            return SimpleResponse.error("获取分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取快速配送分析
     */
    @GetMapping("/fast-delivery/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getFastDeliveryAnalysis(
            @PathVariable String city,
            @RequestParam double threshold,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求城市 {} 快速配送分析，阈值: {}, 开始日期: {}, 限制: {}", city, threshold, startDate, limit);
            List<TimeEfficiencyDTO> data = timeEfficiencyService.getFastDeliveryAnalysis(city, threshold, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取快速配送分析失败", e);
            return SimpleResponse.error("获取分析失败: " + e.getMessage());
        }
    }

    /**
     * 获取时间效率汇总统计
     */
    @GetMapping("/summary/{city}")
    public SimpleResponse<Map<String, Object>> getTimeEfficiencySummary(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的时间效率汇总统计", city, startDate);
            Map<String, Object> data = timeEfficiencyService.getTimeEfficiencySummary(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取时间效率汇总统计失败", e);
            return SimpleResponse.error("获取汇总统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新时间效率数据
     */
    @GetMapping("/latest/{city}")
    public SimpleResponse<TimeEfficiencyDTO> getLatestTimeEfficiency(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的最新时间效率数据", city);
            TimeEfficiencyDTO data = timeEfficiencyService.getLatestTimeEfficiency(city);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取最新时间效率数据失败", e);
            return SimpleResponse.error("获取最新数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市间时间效率对比
     */
    @GetMapping("/comparison")
    public SimpleResponse<List<Map<String, Object>>> getCityTimeEfficiencyComparison(
            @RequestParam List<String> cities,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市间时间效率对比，城市: {}, 时间范围: {} 到 {}", cities, startDate, endDate);
            List<Map<String, Object>> data = timeEfficiencyService.getCityTimeEfficiencyComparison(cities, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取城市间时间效率对比失败", e);
            return SimpleResponse.error("获取对比失败: " + e.getMessage());
        }
    }

    /**
     * 更新时间效率数据
     */
    @PutMapping
    public SimpleResponse<String> updateTimeEfficiency(@RequestBody TimeEfficiencyMetrics metrics) {
        try {
            log.info("更新时间效率数据: city={}, date={}, totalDeliveries={}",
                    metrics.getCity(), metrics.getDate(), metrics.getTotalDeliveries());
            int result = timeEfficiencyService.updateTimeEfficiency(metrics);
            return SimpleResponse.success("更新成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("更新时间效率数据失败", e);
            return SimpleResponse.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 清理旧数据
     */
    @DeleteMapping("/cleanup")
    public SimpleResponse<String> cleanupOldData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {
        try {
            log.info("清理 {} 之前的旧数据", cutoffDate);
            int result = timeEfficiencyService.cleanupOldData(cutoffDate);
            return SimpleResponse.success("清理完成，删除行数: " + result);
        } catch (Exception e) {
            log.error("清理旧数据失败", e);
            return SimpleResponse.error("清理失败: " + e.getMessage());
        }
    }

    /**
     * 统计记录数
     */
    @GetMapping("/count/{city}")
    public SimpleResponse<Integer> countByCity(@PathVariable String city) {
        try {
            log.info("统计城市 {} 的时间效率记录数", city);
            int count = timeEfficiencyService.countByCity(city);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("统计记录数失败", e);
            return SimpleResponse.error("统计失败: " + e.getMessage());
        }
    }
}