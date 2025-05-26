package com.logistics.service.controller;

import com.logistics.service.dto.PredictiveAnalysisDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.PredictiveAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/predictive-analysis")
@CrossOrigin(origins = "*")
public class PredictiveAnalysisController {

    @Autowired
    private PredictiveAnalysisService predictiveAnalysisService;

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
     * 获取小时级预测数据
     */
    @GetMapping("/hourly/{city}")
    public SimpleResponse<List<PredictiveAnalysisDTO>> getHourlyPredictions(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的小时级预测数据", city, date);
            List<PredictiveAnalysisDTO> predictions = predictiveAnalysisService.getHourlyPredictions(city, date);
            return SimpleResponse.success(predictions);
        } catch (Exception e) {
            log.error("获取小时预测数据失败", e);
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
}