package com.logistics.service.controller;

import com.logistics.service.dto.CostAnalysisDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.CostAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cost-analysis")
@CrossOrigin(origins = "*")
public class CostAnalysisController {

    @Autowired
    private CostAnalysisService costAnalysisService;

    /**
     * 获取今日成本分析
     */
    @GetMapping("/today/{city}")
    public SimpleResponse<List<CostAnalysisDTO>> getTodayCostAnalysis(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的今日成本分析", city);
            List<CostAnalysisDTO> data = costAnalysisService.getTodayCostAnalysis(city);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取今日成本分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定时间范围的成本分析
     */
    @GetMapping("/range/{city}")
    public SimpleResponse<List<CostAnalysisDTO>> getCostAnalysisByRange(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 时间范围 {} 到 {} 的成本分析", city, startDate, endDate);
            List<CostAnalysisDTO> data = costAnalysisService.getCostAnalysisByCity(city, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取成本分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送员成本分析
     */
    @GetMapping("/courier/{courierId}")
    public SimpleResponse<List<CostAnalysisDTO>> getCostAnalysisByCourier(
            @PathVariable String courierId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求配送员 {} 时间范围 {} 到 {} 的成本分析", courierId, startDate, endDate);
            List<CostAnalysisDTO> data = costAnalysisService.getCostAnalysisByCourier(courierId, startDate, endDate);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取配送员成本分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }
}