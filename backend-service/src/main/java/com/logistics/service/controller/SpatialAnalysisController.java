package com.logistics.service.controller;

import com.logistics.service.dto.SpatialAnalysisDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.SpatialAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/spatial-analysis")
@CrossOrigin(origins = "*")
public class SpatialAnalysisController {

    @Autowired
    private SpatialAnalysisService spatialAnalysisService;

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
     * 获取热点区域分析
     */
    @GetMapping("/hotspots/{city}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getHotspotAnalysis(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的热点区域分析", city, date);
            List<SpatialAnalysisDTO> hotspots = spatialAnalysisService.getHotspotAnalysis(city, date);
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
     * 按AOI类型获取空间分析
     */
    @GetMapping("/aoi-type/{city}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getSpatialAnalysisByAoiType(
            @PathVariable String city,
            @RequestParam Integer aoiType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} AOI类型 {} 日期 {} 的空间分析", city, aoiType, date);
            List<SpatialAnalysisDTO> analysis = spatialAnalysisService.getSpatialAnalysisByAoiType(city, aoiType, date);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("按AOI类型获取空间分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 按AOI ID获取详细分析
     */
    @GetMapping("/aoi/{aoiId}")
    public SimpleResponse<List<SpatialAnalysisDTO>> getAnalysisByAoiId(
            @PathVariable String aoiId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求AOI {} 时间范围 {} 到 {} 的详细分析", aoiId, startDate, endDate);
            List<SpatialAnalysisDTO> analysis = spatialAnalysisService.getAnalysisByAoiId(aoiId, startDate, endDate);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("按AOI ID获取分析失败", e);
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
}