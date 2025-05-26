package com.logistics.service.controller;

import com.logistics.service.dto.ComprehensiveReportDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.ComprehensiveReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comprehensive-reports")
@CrossOrigin(origins = "*")
public class ComprehensiveReportController {

    @Autowired
    private ComprehensiveReportService comprehensiveReportService;

    /**
     * 获取最新日报
     */
    @GetMapping("/daily/{city}")
    public SimpleResponse<List<ComprehensiveReportDTO>> getLatestDailyReports(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的最新日报", city);
            List<ComprehensiveReportDTO> reports = comprehensiveReportService.getLatestDailyReports(city);
            return SimpleResponse.success(reports);
        } catch (Exception e) {
            log.error("获取日报失败", e);
            return SimpleResponse.error("获取日报失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新周报
     */
    @GetMapping("/weekly/{city}")
    public SimpleResponse<List<ComprehensiveReportDTO>> getLatestWeeklyReports(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的最新周报", city);
            List<ComprehensiveReportDTO> reports = comprehensiveReportService.getLatestWeeklyReports(city);
            return SimpleResponse.success(reports);
        } catch (Exception e) {
            log.error("获取周报失败", e);
            return SimpleResponse.error("获取周报失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定时间范围的报告
     */
    @GetMapping("/{city}")
    public SimpleResponse<List<ComprehensiveReportDTO>> getReports(
            @PathVariable String city,
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 类型 {} 时间范围 {} 到 {} 的报告", city, reportType, startDate, endDate);
            List<ComprehensiveReportDTO> reports = comprehensiveReportService.getReportsByCity(city, reportType, startDate, endDate);
            return SimpleResponse.success(reports);
        } catch (Exception e) {
            log.error("获取报告失败", e);
            return SimpleResponse.error("获取报告失败: " + e.getMessage());
        }
    }
}