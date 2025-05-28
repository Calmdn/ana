package com.logistics.service.controller;

import com.logistics.service.dto.KpiDataDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.KpiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/kpi")
@CrossOrigin(origins = "*")
public class KpiController {

    @Autowired
    private KpiService kpiService;

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public SimpleResponse<String> health() {
        try {
            String healthStatus = kpiService.getSystemHealth();
            return SimpleResponse.success(healthStatus);
        } catch (Exception e) {
            log.error("健康检查失败", e);
            return SimpleResponse.error("健康检查失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定城市的今日KPI
     */
    @GetMapping("/today/{city}")
    public SimpleResponse<List<KpiDataDTO>> getTodayKpi(@PathVariable String city) {
        try {
            log.info("请求城市 {} 的今日KPI数据", city);
            List<KpiDataDTO> kpiData = kpiService.getTodayKpiByCity(city);

            if (kpiData.isEmpty()) {
                return SimpleResponse.error("暂无 " + city + " 的今日KPI数据");
            }

            return SimpleResponse.success(kpiData);
        } catch (Exception e) {
            log.error("获取KPI数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定日期的KPI数据
     */
    @GetMapping("/date/{city}")
    public SimpleResponse<List<KpiDataDTO>> getKpiByDate(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now();
            }
            log.info("请求城市 {} 日期 {} 的KPI数据", city, date);
            List<KpiDataDTO> kpiData = kpiService.getKpiByDate(city, date);
            return SimpleResponse.success(kpiData);
        } catch (Exception e) {
            log.error("获取指定日期KPI数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近几天的KPI数据
     */
    @GetMapping("/recent/{city}")
    public SimpleResponse<List<KpiDataDTO>> getRecentKpi(
            @PathVariable String city,
            @RequestParam(defaultValue = "7") int days) {
        try {
            log.info("请求城市 {} 最近 {} 天的KPI数据", city, days);
            List<KpiDataDTO> kpiData = kpiService.getRecentKpi(city, days);
            return SimpleResponse.success(kpiData);
        } catch (Exception e) {
            log.error("获取最近KPI数据失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 清理旧数据
     */
    @DeleteMapping("/cleanup")
    public SimpleResponse<String> cleanupOldData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cutoffDate) {
        try {
            log.info("清理 {} 之前的旧KPI数据", cutoffDate);
            int result = kpiService.cleanupOldData(cutoffDate);
            return SimpleResponse.success("清理完成，删除行数: " + result);
        } catch (Exception e) {
            log.error("清理旧KPI数据失败", e);
            return SimpleResponse.error("清理失败: " + e.getMessage());
        }
    }

    /**
     * 统计记录数
     */
    @GetMapping("/count/{city}")
    public SimpleResponse<Integer> countByCity(@PathVariable String city) {
        try {
            log.info("统计城市 {} 的KPI记录数", city);
            int count = kpiService.countByCity(city);
            return SimpleResponse.success(count);
        } catch (Exception e) {
            log.error("统计记录数失败", e);
            return SimpleResponse.error("统计失败: " + e.getMessage());
        }
    }
}