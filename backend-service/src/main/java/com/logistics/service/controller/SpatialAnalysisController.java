package com.logistics.service.controller;

import com.logistics.service.dao.entity.SpatialAnalysisMetrics;
import com.logistics.service.dto.SpatialAnalysisDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.SpatialAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/spatial-analysis")
@CrossOrigin(origins = "*")
public class SpatialAnalysisController {

    @Autowired
    private SpatialAnalysisService spatialAnalysisService;

    /**
     * 保存空间分析数据
     */
    @PostMapping
    public SimpleResponse<String> saveSpatialAnalysis(@RequestBody SpatialAnalysisMetrics metrics) {
        try {
            log.info("保存空间分析数据: city={}, date={}, grid=({},{})",
                    metrics.getCity(), metrics.getDate(), metrics.getLngGrid(), metrics.getLatGrid());
            int result = spatialAnalysisService.saveSpatialAnalysis(metrics);
            return SimpleResponse.success("保存成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("保存空间分析数据失败", e);
            return SimpleResponse.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存空间分析数据
     */
    @PostMapping("/batch")
    public SimpleResponse<String> batchSaveSpatialAnalysis(@RequestBody List<SpatialAnalysisMetrics> metricsList) {
        try {
            log.info("批量保存空间分析数据，数量: {}", metricsList.size());
            int result = spatialAnalysisService.batchSaveSpatialAnalysis(metricsList);
            return SimpleResponse.success("批量保存成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("批量保存空间分析数据失败", e);
            return SimpleResponse.error("批量保存失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日空间分析
     */
    @GetMapping("/today/{city}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getTodaySpatialAnalysis(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的今日空间分析", city);
            List<SpatialAnalysisDTO> analysis = spatialAnalysisService.getTodaySpatialAnalysis(city);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("获取今日空间分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定日期的空间分析
     */
    @GetMapping("/date/{city}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getSpatialAnalysisByDate(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的空间分析", city, date);
            List<SpatialAnalysisDTO> analysis = spatialAnalysisService.getSpatialAnalysisByDate(city, date);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("获取指定日期空间分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取热点区域分析
     */
    @GetMapping("/hotspots/{city}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getHotspotAnalysis(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的热点区域分析，限制 {} 条", city, date, limit);
            List<SpatialAnalysisDTO> hotspots = spatialAnalysisService.getHotspotAnalysis(city, date, limit);
            return SimpleResponse.success(hotspots);
        } catch (Exception e) {
            log.error("获取热点区域分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取密度分析
     */
    @GetMapping("/density/{city}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getDensityAnalysis(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的密度分析", city, date);
            List<SpatialAnalysisDTO> density = spatialAnalysisService.getDensityAnalysis(city, date);
            return SimpleResponse.success(density);
        } catch (Exception e) {
            log.error("获取密度分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定时间范围的空间分析
     */
    @GetMapping("/range/{city}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getSpatialAnalysisByRange(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 时间范围 {} 到 {} 的空间分析", city, startDate, endDate);
            List<SpatialAnalysisDTO> analysis = spatialAnalysisService.getSpatialAnalysisByCity(city, startDate, endDate);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("获取空间分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据地理范围获取空间分析
     */
    @GetMapping("/geo-range/{city}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getSpatialAnalysisByGeoRange(
            @PathVariable String city,
            @RequestParam Double minLng, @RequestParam Double maxLng,
            @RequestParam Double minLat, @RequestParam Double maxLat,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 地理范围 ({},{}) 到 ({},{}) 时间范围 {} 到 {} 的空间分析",
                    city, minLng, minLat, maxLng, maxLat, startDate, endDate);
            List<SpatialAnalysisDTO> analysis = spatialAnalysisService.getSpatialAnalysisByGeoRange(
                    city, minLng, maxLng, minLat, maxLat, startDate, endDate);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("根据地理范围获取空间分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送密度热点
     */
    @GetMapping("/heatmap/density/{city}")
    public SimpleResponse<List<Map<String, Object>>> getDeliveryDensityHotspots(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            log.info("请求城市 {} 从 {} 开始的配送密度热点，限制 {} 条", city, startDate, limit);
            List<Map<String, Object>> data = spatialAnalysisService.getDeliveryDensityHotspots(city, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取配送密度热点失败", e);
            return SimpleResponse.error("获取热点数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送时间热图数据
     */
    @GetMapping("/heatmap/delivery-time/{city}")
    public SimpleResponse<List<Map<String, Object>>> getDeliveryTimeHeatmap(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的配送时间热图数据", city, startDate);
            List<Map<String, Object>> data = spatialAnalysisService.getDeliveryTimeHeatmap(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取配送时间热图数据失败", e);
            return SimpleResponse.error("获取热图数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取空间分布统计
     */
    @GetMapping("/stats/distribution/{city}")
    public SimpleResponse<List<Map<String, Object>>> getSpatialDistributionStats(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的空间分布统计", city, startDate);
            List<Map<String, Object>> data = spatialAnalysisService.getSpatialDistributionStats(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取空间分布统计失败", e);
            return SimpleResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取网格聚合数据
     */
    @GetMapping("/grid-aggregation/{city}")
    public SimpleResponse<List<Map<String, Object>>> getGridAggregation(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0.01") Double gridSize) {
        try {
            log.info("请求城市 {} 日期 {} 网格大小 {} 的聚合数据", city, date, gridSize);
            List<Map<String, Object>> data = spatialAnalysisService.getGridAggregation(city, date, gridSize);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取网格聚合数据失败", e);
            return SimpleResponse.error("获取聚合数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取空间汇总统计
     */
    @GetMapping("/summary/{city}")
    public SimpleResponse<Map<String, Object>> getSpatialSummary(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的空间汇总统计", city, startDate);
            Map<String, Object> data = spatialAnalysisService.getSpatialSummary(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取空间汇总统计失败", e);
            return SimpleResponse.error("获取汇总数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送员空间分布
     */
    @GetMapping("/courier-distribution/{city}")
    public SimpleResponse<List<Map<String, Object>>> getCourierSpatialDistribution(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "30") int limit) {
        try {
            log.info("请求城市 {} 从 {} 开始的配送员空间分布，限制 {} 条", city, startDate, limit);
            List<Map<String, Object>> data = spatialAnalysisService.getCourierSpatialDistribution(city, startDate, limit);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取配送员空间分布失败", e);
            return SimpleResponse.error("获取分布数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市间空间对比
     */
    @GetMapping("/comparison")
    public SimpleResponse<List<Map<String, Object>>> getCitySpatialComparison(
            @RequestParam List<String> cities,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市间空间对比，城市: {}, 时间范围: {} 到 {}", cities, startDate, endDate);
            List<Map<String, Object>> data = spatialAnalysisService.getCitySpatialComparison(cities, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取城市间空间对比失败", e);
            return SimpleResponse.error("获取对比数据失败: " + e.getMessage());
        }
    }

    /**
     * 更新空间分析数据
     */
    @PutMapping
    public SimpleResponse<String> updateSpatialAnalysis(@RequestBody SpatialAnalysisMetrics metrics) {
        try {
            log.info("更新空间分析数据: city={}, date={}, grid=({},{})",
                    metrics.getCity(), metrics.getDate(), metrics.getLngGrid(), metrics.getLatGrid());
            int result = spatialAnalysisService.updateSpatialAnalysis(metrics);
            return SimpleResponse.success("更新成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("更新空间分析数据失败", e);
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
            int result = spatialAnalysisService.cleanupOldData(cutoffDate);
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
            log.info("统计城市 {} 的空间分析记录数", city);
            int count = spatialAnalysisService.countByCity(city);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("统计记录数失败", e);
            return SimpleResponse.error("统计失败: " + e.getMessage());
        }
    }
}