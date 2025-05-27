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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/comprehensive-reports")
@CrossOrigin(origins = "*")
public class ComprehensiveReportController {

    @Autowired
    private ComprehensiveReportService comprehensiveReportService;

    // ==================== 基础CRUD操作 ====================

    /**
     * 创建或更新报告
     */
    @PostMapping
    public SimpleResponse<String> createOrUpdateReport(@RequestBody ComprehensiveReportDTO reportDTO) {
        try {
            log.info("请求创建/更新报告，城市: {}, 日期: {}, 类型: {}",
                    reportDTO.getCity(), reportDTO.getDate(), reportDTO.getReportType());
            boolean success = comprehensiveReportService.saveReport(reportDTO);
            if (success) {
                return SimpleResponse.success("报告保存成功");
            } else {
                return SimpleResponse.error("报告保存失败");
            }
        } catch (Exception e) {
            log.error("保存报告失败", e);
            return SimpleResponse.error("保存报告失败: " + e.getMessage());
        }
    }

    /**
     * 根据城市、日期和类型获取特定报告
     */
    @GetMapping("/specific")
    public SimpleResponse<ComprehensiveReportDTO> getSpecificReport(
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String reportType) {
        try {
            log.info("请求特定报告，城市: {}, 日期: {}, 类型: {}", city, date, reportType);
            ComprehensiveReportDTO report = comprehensiveReportService.getReportByCityDateAndType(city, date, reportType);
            if (report != null) {
                return SimpleResponse.success(report);
            } else {
                return SimpleResponse.error("报告不存在");
            }
        } catch (Exception e) {
            log.error("获取特定报告失败", e);
            return SimpleResponse.error("获取报告失败: " + e.getMessage());
        }
    }

    /**
     * 更新报告
     */
    @PutMapping
    public SimpleResponse<String> updateReport(@RequestBody ComprehensiveReportDTO reportDTO) {
        try {
            log.info("请求更新报告，城市: {}, 日期: {}, 类型: {}",
                    reportDTO.getCity(), reportDTO.getDate(), reportDTO.getReportType());
            boolean success = comprehensiveReportService.updateReport(reportDTO);
            if (success) {
                return SimpleResponse.success("报告更新成功");
            } else {
                return SimpleResponse.error("报告更新失败");
            }
        } catch (Exception e) {
            log.error("更新报告失败", e);
            return SimpleResponse.error("更新报告失败: " + e.getMessage());
        }
    }

    // ==================== 原有查询接口 ====================

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

    // ==================== 新增查询接口 ====================

    /**
     * 根据城市和日期范围获取报告（不限类型）
     */
    @GetMapping("/range/{city}")
    public SimpleResponse<List<ComprehensiveReportDTO>> getReportsByDateRange(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 时间范围 {} 到 {} 的所有报告", city, startDate, endDate);
            List<ComprehensiveReportDTO> reports = comprehensiveReportService.getReportsByDateRange(city, startDate, endDate);
            return SimpleResponse.success(reports);
        } catch (Exception e) {
            log.error("获取报告失败", e);
            return SimpleResponse.error("获取报告失败: " + e.getMessage());
        }
    }

    /**
     * 根据报告类型获取报告
     */
    @GetMapping("/type/{reportType}")
    public SimpleResponse<List<ComprehensiveReportDTO>> getReportsByType(@PathVariable String reportType) {
        try {
            log.info("请求类型 {} 的所有报告", reportType);
            List<ComprehensiveReportDTO> reports = comprehensiveReportService.getReportsByType(reportType);
            return SimpleResponse.success(reports);
        } catch (Exception e) {
            log.error("获取报告失败", e);
            return SimpleResponse.error("获取报告失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市最新报告
     */
    @GetMapping("/latest/{city}")
    public SimpleResponse<ComprehensiveReportDTO> getLatestReport(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的最新报告", city);
            ComprehensiveReportDTO report = comprehensiveReportService.getLatestReportByCity(city);
            if (report != null) {
                return SimpleResponse.success(report);
            } else {
                return SimpleResponse.error("未找到报告");
            }
        } catch (Exception e) {
            log.error("获取最新报告失败", e);
            return SimpleResponse.error("获取报告失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有城市列表
     */
    @GetMapping("/cities")
    public SimpleResponse<List<String>> getAllCities() {
        try {
            log.info("请求所有城市列表");
            List<String> cities = comprehensiveReportService.getAllCities();
            return SimpleResponse.success(cities);
        } catch (Exception e) {
            log.error("获取城市列表失败", e);
            return SimpleResponse.error("获取城市列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据日期获取所有城市报告
     */
    @GetMapping("/date/{date}")
    public SimpleResponse<List<ComprehensiveReportDTO>> getReportsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            log.info("请求日期 {} 的所有城市报告", date);
            List<ComprehensiveReportDTO> reports = comprehensiveReportService.getReportsByDate(date);
            return SimpleResponse.success(reports);
        } catch (Exception e) {
            log.error("获取报告失败", e);
            return SimpleResponse.error("获取报告失败: " + e.getMessage());
        }
    }

    // ==================== 统计分析接口 ====================

    /**
     * 获取城市报告趋势
     */
    @GetMapping("/trend/{city}")
    public SimpleResponse<List<Map<String, Object>>> getCityReportTrend(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        try {
            log.info("请求城市 {} 从 {} 开始的报告趋势", city, startDate);
            List<Map<String, Object>> trend = comprehensiveReportService.getCityReportTrend(city, startDate);
            return SimpleResponse.success(trend);
        } catch (Exception e) {
            log.error("获取报告趋势失败", e);
            return SimpleResponse.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取城市效率排行
     */
    @GetMapping("/ranking/efficiency")
    public SimpleResponse<List<Map<String, Object>>> getCityEfficiencyRanking(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("请求从 {} 开始的城市效率排行，限制 {} 条", startDate, limit);
            List<Map<String, Object>> ranking = comprehensiveReportService.getCityEfficiencyRanking(startDate, limit);
            return SimpleResponse.success(ranking);
        } catch (Exception e) {
            log.error("获取效率排行失败", e);
            return SimpleResponse.error("获取排行数据失败: " + e.getMessage());
        }
    }

    /**
     * 统计指定时间范围内的总配送量
     */
    @GetMapping("/stats/deliveries/{city}")
    public SimpleResponse<Long> getTotalDeliveries(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求城市 {} 时间范围 {} 到 {} 的总配送量", city, startDate, endDate);
            Long totalDeliveries = comprehensiveReportService.getTotalDeliveriesByDateRange(city, startDate, endDate);
            return SimpleResponse.success(totalDeliveries);
        } catch (Exception e) {
            log.error("获取总配送量失败", e);
            return SimpleResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取配送效率统计
     */
    @GetMapping("/stats/efficiency")
    public SimpleResponse<List<Map<String, Object>>> getDeliveryEfficiencyStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            log.info("请求时间范围 {} 到 {} 的配送效率统计", startDate, endDate);
            List<Map<String, Object>> stats = comprehensiveReportService.getDeliveryEfficiencyStats(startDate, endDate);
            return SimpleResponse.success(stats);
        } catch (Exception e) {
            log.error("获取效率统计失败", e);
            return SimpleResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 统计报告数量
     */
    @GetMapping("/count")
    public SimpleResponse<Integer> getReportsCount(
            @RequestParam String city,
            @RequestParam String reportType) {
        try {
            log.info("请求城市 {} 类型 {} 的报告数量", city, reportType);
            int count = comprehensiveReportService.countReportsByCityAndType(city, reportType);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("获取报告数量失败", e);
            return SimpleResponse.error("获取统计失败: " + e.getMessage());
        }
    }

    // ==================== 数据维护接口 ====================

    /**
     * 清理旧报告数据
     */
    @DeleteMapping("/cleanup")
    public SimpleResponse<String> cleanupOldReports(
            @RequestParam(defaultValue = "90") int daysToKeep) {
        try {
            log.info("请求清理 {} 天前的旧报告数据", daysToKeep);
            int deleted = comprehensiveReportService.cleanupOldReports(daysToKeep);
            return SimpleResponse.success(String.format("成功清理 %d 条旧报告数据", deleted));
        } catch (Exception e) {
            log.error("清理旧报告数据失败", e);
            return SimpleResponse.error("清理数据失败: " + e.getMessage());
        }
    }
}