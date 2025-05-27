package com.logistics.service.controller;

import com.logistics.service.dao.entity.PredictiveAnalysisData;
import com.logistics.service.dto.PredictiveAnalysisDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.PredictiveAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/predictive-analysis")
@CrossOrigin(origins = "*")
public class PredictiveAnalysisController {

    @Autowired
    private PredictiveAnalysisService predictiveAnalysisService;

    /**
     * 保存预测分析数据
     */
    @PostMapping
    public SimpleResponse<String> savePredictiveAnalysis(@RequestBody PredictiveAnalysisData data) {
        try {
            log.info("保存预测分析数据: city={}, dsDate={}, dataType={}",
                    data.getCity(), data.getDsDate(), data.getDataType());
            int result = predictiveAnalysisService.savePredictiveAnalysis(data);
            return SimpleResponse.success("保存成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("保存预测分析数据失败", e);
            return SimpleResponse.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存预测分析数据
     */
    @PostMapping("/batch")
    public SimpleResponse<String> batchSavePredictiveAnalysis(@RequestBody List<PredictiveAnalysisData> dataList) {
        try {
            log.info("批量保存预测分析数据，数量: {}", dataList.size());
            int result = predictiveAnalysisService.batchSavePredictiveAnalysis(dataList);
            return SimpleResponse.success("批量保存成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("批量保存预测分析数据失败", e);
            return SimpleResponse.error("批量保存失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新预测数据
     */
    @GetMapping("/latest/{city}")
    public SimpleResponse<List<PredictiveAnalysisDTO>> getLatestPredictions(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的最新预测数据", city);
            List<PredictiveAnalysisDTO> predictions = predictiveAnalysisService.getLatestPredictions(city);
            return SimpleResponse.success(predictions);
        } catch (Exception e) {
            log.error("获取最新预测数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 按类型获取最新预测数据
     */
    @GetMapping("/latest/{city}/{dataType}")
    public SimpleResponse<List<PredictiveAnalysisDTO>> getLatestPredictionsByType(
            @PathVariable String city,
            @PathVariable String dataType,
            @RequestParam(defaultValue = "24") int limit) {
        try {
            log.info("请求城市 {} 类型 {} 的最新预测数据，限制 {} 条", city, dataType, limit);
            List<PredictiveAnalysisDTO> predictions = predictiveAnalysisService.getLatestPredictionsByType(city, dataType, limit);
            return SimpleResponse.success(predictions);
        } catch (Exception e) {
            log.error("获取最新预测数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取历史趋势数据
     */
    @GetMapping("/trends/{city}")
    public SimpleResponse<List<PredictiveAnalysisDTO>> getHistoricalTrends(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的历史趋势数据", city);
            List<PredictiveAnalysisDTO> trends = predictiveAnalysisService.getHistoricalTrends(city);
            return SimpleResponse.success(trends);
        } catch (Exception e) {
            log.error("获取历史趋势数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取容量分析数据
     */
    @GetMapping("/capacity/{city}")
    public SimpleResponse<List<PredictiveAnalysisDTO>> getCapacityAnalysis(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的容量分析数据", city);
            List<PredictiveAnalysisDTO> capacity = predictiveAnalysisService.getCapacityAnalysis(city);
            return SimpleResponse.success(capacity);
        } catch (Exception e) {
            log.error("获取容量分析数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定日期的预测数据
     */
    @GetMapping("/date/{city}")
    public SimpleResponse<List<PredictiveAnalysisDTO>> getPredictiveAnalysisByDate(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的预测数据", city, date);
            List<PredictiveAnalysisDTO> predictions = predictiveAnalysisService.getPredictiveAnalysisByDate(city, date);
            return SimpleResponse.success(predictions);
        } catch (Exception e) {
            log.error("获取指定日期预测数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定类型和时间范围的预测分析
     */
    @GetMapping("/custom/{city}")
    public SimpleResponse<List<PredictiveAnalysisDTO>> getCustomPredictiveAnalysis(
            @PathVariable String city,
            @RequestParam String dataType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 类型 {} 时间范围 {} 到 {} 的预测分析", city, dataType, startDate, endDate);
            List<PredictiveAnalysisDTO> analysis = predictiveAnalysisService.getPredictiveAnalysisByCity(city, dataType, startDate, endDate);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("获取预测分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 多条件搜索预测数据
     */
    @GetMapping("/search")
    public SimpleResponse<List<PredictiveAnalysisDTO>> searchPredictiveAnalysis(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String regionId,
            @RequestParam(required = false) String dataType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("多条件搜索预测数据: city={}, regionId={}, dataType={}, startDate={}, endDate={}",
                    city, regionId, dataType, startDate, endDate);
            List<PredictiveAnalysisDTO> data = predictiveAnalysisService.getPredictiveAnalysisByConditions(
                    city, regionId, dataType, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("多条件搜索预测数据失败", e);
            return SimpleResponse.error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单量趋势
     */
    @GetMapping("/trend/order-volume/{city}")
    public SimpleResponse<List<Map<String, Object>>> getOrderVolumeTrend(
            @PathVariable String city,
            @RequestParam(required = false) String dataType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 类型 {} 从 {} 开始的订单量趋势", city, dataType, startDate);
            List<Map<String, Object>> data = predictiveAnalysisService.getOrderVolumeTrend(city, dataType, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取订单量趋势失败", e);
            return SimpleResponse.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取小时分布分析
     */
    @GetMapping("/distribution/hourly/{city}")
    public SimpleResponse<List<Map<String, Object>>> getHourlyDistribution(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的小时分布分析", city, startDate);
            List<Map<String, Object>> data = predictiveAnalysisService.getHourlyDistribution(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取小时分布分析失败", e);
            return SimpleResponse.error("获取分布数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取效率预测趋势
     */
    @GetMapping("/trend/efficiency/{city}")
    public SimpleResponse<List<Map<String, Object>>> getEfficiencyTrend(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的效率预测趋势", city, startDate);
            List<Map<String, Object>> data = predictiveAnalysisService.getEfficiencyTrend(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取效率预测趋势失败", e);
            return SimpleResponse.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取容量分析统计
     */
    @GetMapping("/stats/capacity/{city}")
    public SimpleResponse<List<Map<String, Object>>> getCapacityAnalysisStats(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的容量分析统计", city, startDate);
            List<Map<String, Object>> data = predictiveAnalysisService.getCapacityAnalysisStats(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取容量分析统计失败", e);
            return SimpleResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据类型统计
     */
    @GetMapping("/stats/data-type/{city}")
    public SimpleResponse<List<Map<String, Object>>> getDataTypeStats(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的数据类型统计", city, startDate);
            List<Map<String, Object>> data = predictiveAnalysisService.getDataTypeStats(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取数据类型统计失败", e);
            return SimpleResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取预测汇总统计
     */
    @GetMapping("/summary/{city}")
    public SimpleResponse<Map<String, Object>> getPredictiveSummary(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的预测汇总统计", city, startDate);
            Map<String, Object> data = predictiveAnalysisService.getPredictiveSummary(city, startDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取预测汇总统计失败", e);
            return SimpleResponse.error("获取汇总数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市间预测对比
     */
    @GetMapping("/comparison")
    public SimpleResponse<List<Map<String, Object>>> getCityPredictiveComparison(
            @RequestParam List<String> cities,
            @RequestParam(required = false) String dataType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市间预测对比，城市: {}, 类型: {}, 时间范围: {} 到 {}", cities, dataType, startDate, endDate);
            List<Map<String, Object>> data = predictiveAnalysisService.getCityPredictiveComparison(cities, dataType, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取城市间预测对比失败", e);
            return SimpleResponse.error("获取对比数据失败: " + e.getMessage());
        }
    }

    /**
     * 更新预测分析数据
     */
    @PutMapping
    public SimpleResponse<String> updatePredictiveAnalysis(@RequestBody PredictiveAnalysisData data) {
        try {
            log.info("更新预测分析数据: city={}, dsDate={}, dataType={}",
                    data.getCity(), data.getDsDate(), data.getDataType());
            int result = predictiveAnalysisService.updatePredictiveAnalysis(data);
            return SimpleResponse.success("更新成功，影响行数: " + result);
        } catch (Exception e) {
            log.error("更新预测分析数据失败", e);
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
            int result = predictiveAnalysisService.cleanupOldData(cutoffDate);
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
    public SimpleResponse<Integer> countByCityAndType(
            @PathVariable String city,
            @RequestParam(required = false) String dataType) {
        try {
            log.info("统计城市 {} 类型 {} 的记录数", city, dataType);
            int count = predictiveAnalysisService.countByCityAndType(city, dataType);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("统计记录数失败", e);
            return SimpleResponse.error("统计失败: " + e.getMessage());
        }
    }
}