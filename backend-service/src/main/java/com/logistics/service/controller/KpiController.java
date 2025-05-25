package com.logistics.service.controller;

import com.logistics.service.dto.KpiDataDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.KpiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 简单的测试接口
     */
    @GetMapping("/test")
    public SimpleResponse<String> test() {
        return SimpleResponse.success("🎉 KPI API 测试成功! 当前时间: " + java.time.LocalDateTime.now());
    }
}