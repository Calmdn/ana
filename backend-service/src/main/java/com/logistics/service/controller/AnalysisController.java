package com.logistics.service.controller;

import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.SparkJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    @Autowired
    private SparkJobService sparkJobService;

    /**
     * 手动触发分析任务
     */
    @PostMapping("/trigger")
    public SimpleResponse<String> triggerAnalysis() {
        try {
            log.info("🔥 收到手动触发分析任务请求");

            String result = sparkJobService.executeAnalysisJob();

            return SimpleResponse.success("分析任务已触发", result);

        } catch (Exception e) {
            log.error("💥 触发分析任务失败", e);
            return SimpleResponse.error("触发失败: " + e.getMessage());
        }
    }

    /**
     * 检查Spark环境
     */
    @GetMapping("/spark/check")
    public SimpleResponse<String> checkSparkEnvironment() {
        try {
            String result = sparkJobService.checkSparkEnvironment();
            return SimpleResponse.success(result);
        } catch (Exception e) {
            log.error("检查Spark环境失败", e);
            return SimpleResponse.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * 获取分析状态
     */
    @GetMapping("/status")
    public SimpleResponse<String> getAnalysisStatus() {
        return SimpleResponse.success("🚀 分析模块运行正常，时间: " + java.time.LocalDateTime.now());
    }
}