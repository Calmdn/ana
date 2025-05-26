package com.logistics.service.controller;

import com.logistics.service.dto.TimeEfficiencyDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.TimeEfficiencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/time-efficiency")
@CrossOrigin(origins = "*")
public class TimeEfficiencyController {

    @Autowired
    private TimeEfficiencyService timeEfficiencyService;

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
     * 获取小时级时间效率
     */
    @GetMapping("/hourly/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getHourlyTimeEfficiency(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的小时级时间效率", city, date);
            List<TimeEfficiencyDTO> data = timeEfficiencyService.getHourlyTimeEfficiency(city, date);
            return SimpleResponse.success(data);
        } catch (Exception e) {
            log.error("获取小时时间效率失败", e);
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
     * 获取高峰时段分析
     */
    @GetMapping("/peak-hours/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getPeakHourAnalysis(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的高峰时段分析", city, date);
            List<TimeEfficiencyDTO> analysis = timeEfficiencyService.getPeakHourAnalysis(city, date);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("获取高峰时段分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取取件效率分析
     */
    @GetMapping("/pickup/{city}")
    public SimpleResponse<List<TimeEfficiencyDTO>> getPickupEfficiencyAnalysis(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 时间范围 {} 到 {} 的取件效率分析", city, startDate, endDate);
            List<TimeEfficiencyDTO> analysis = timeEfficiencyService.getPickupEfficiencyAnalysis(city, startDate, endDate);
            return SimpleResponse.success(analysis);
        } catch (Exception e) {
            log.error("获取取件效率分析失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
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
}