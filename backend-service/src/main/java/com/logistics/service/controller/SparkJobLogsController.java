package com.logistics.service.controller;

import com.logistics.service.dto.SparkJobLogsDTO;
import com.logistics.service.dto.SimpleResponse;
import com.logistics.service.service.SparkJobLogsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/spark-jobs")
@CrossOrigin(origins = "*")
public class SparkJobLogsController {

    @Autowired
    private SparkJobLogsService sparkJobLogsService;

    /**
     * 获取最近的作业日志
     */
    @GetMapping("/recent")
    public SimpleResponse<List<SparkJobLogsDTO>> getRecentJobs(
            @RequestParam(defaultValue = "50") int limit) {
        try {
            log.info("请求最近 {} 个作业日志", limit);
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getRecentJobLogs(limit);
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("获取最近作业日志失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取失败的作业
     */
    @GetMapping("/failed")
    public SimpleResponse<List<SparkJobLogsDTO>> getFailedJobs() {
        try {
            log.info("请求失败的作业日志");
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getFailedJobs();
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("获取失败作业日志失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取运行中的作业
     */
    @GetMapping("/running")
    public SimpleResponse<List<SparkJobLogsDTO>> getRunningJobs() {
        try {
            log.info("请求运行中的作业日志");
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getRunningJobs();
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("获取运行中作业日志失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取成功的作业
     */
    @GetMapping("/successful")
    public SimpleResponse<List<SparkJobLogsDTO>> getSuccessfulJobs(
            @RequestParam(defaultValue = "50") int limit) {
        try {
            log.info("请求成功的作业日志，限制 {} 条", limit);
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getSuccessfulJobs(limit);
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("获取成功作业日志失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 按作业名称获取日志
     */
    @GetMapping("/by-name/{jobName}")
    public SimpleResponse<List<SparkJobLogsDTO>> getJobsByName(@PathVariable String jobName) {
        try {
            log.info("请求作业名称 {} 的日志", jobName);
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getJobLogsByName(jobName);
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("按作业名称获取日志失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 按状态获取作业日志
     */
    @GetMapping("/by-status/{status}")
    public SimpleResponse<List<SparkJobLogsDTO>> getJobsByStatus(@PathVariable String status) {
        try {
            log.info("请求状态为 {} 的作业日志", status);
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getJobLogsByStatus(status);
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("按状态获取作业日志失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 按时间范围获取作业日志
     */
    @GetMapping("/by-time-range")
    public SimpleResponse<List<SparkJobLogsDTO>> getJobsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            log.info("请求时间范围 {} 到 {} 的作业日志", startTime, endTime);
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getJobLogsByTimeRange(startTime, endTime);
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("按时间范围获取作业日志失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取今日作业日志
     */
    @GetMapping("/today")
    public SimpleResponse<List<SparkJobLogsDTO>> getTodayJobs() {
        try {
            log.info("请求今日作业日志");
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getTodayJobs();
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("获取今日作业日志失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取长时间运行的作业
     */
    @GetMapping("/long-running")
    public SimpleResponse<List<SparkJobLogsDTO>> getLongRunningJobs(
            @RequestParam(defaultValue = "30") int thresholdMinutes) {
        try {
            log.info("请求执行时间超过 {} 分钟的作业", thresholdMinutes);
            List<SparkJobLogsDTO> jobs = sparkJobLogsService.getLongRunningJobs(thresholdMinutes);
            return SimpleResponse.success(jobs);
        } catch (Exception e) {
            log.error("获取长时间运行作业失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取作业详情
     */
    @GetMapping("/{id}")
    public SimpleResponse<SparkJobLogsDTO> getJobById(@PathVariable Long id) {
        try {
            log.info("请求作业 ID {} 的详情", id);
            SparkJobLogsDTO job = sparkJobLogsService.getJobById(id);
            if (job != null) {
                return SimpleResponse.success(job);
            } else {
                return SimpleResponse.error("未找到指定的作业");
            }
        } catch (Exception e) {
            log.error("根据ID获取作业失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业统计信息
     */
    @GetMapping("/statistics")
    public SimpleResponse<Map<String, Object>> getJobStatistics() {
        try {
            log.info("请求作业统计信息");
            Map<String, Object> statistics = sparkJobLogsService.getJobStatistics();
            return SimpleResponse.success(statistics);
        } catch (Exception e) {
            log.error("获取作业统计失败", e);
            return SimpleResponse.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取作业执行趋势
     */
    @GetMapping("/trend")
    public SimpleResponse<List<Map<String, Object>>> getJobExecutionTrend(
            @RequestParam(defaultValue = "7") int days) {
        try {
            log.info("请求最近 {} 天的作业执行趋势", days);
            List<Map<String, Object>> trend = sparkJobLogsService.getJobExecutionTrend(days);
            return SimpleResponse.success(trend);
        } catch (Exception e) {
            log.error("获取作业执行趋势失败", e);
            return SimpleResponse.error("获取数据失败: " + e.getMessage());
        }
    }

    /**
     * 清理旧的作业日志
     */
    @DeleteMapping("/cleanup")
    public SimpleResponse<String> cleanupOldJobs(
            @RequestParam(defaultValue = "30") int daysToKeep) {
        try {
            log.info("尝试清理 {} 天前的作业日志", daysToKeep);
            int deletedCount = sparkJobLogsService.cleanupOldJobs(daysToKeep);
            if (deletedCount > 0) {
                return SimpleResponse.success(String.format("成功清理 %d 条旧作业日志", deletedCount));
            } else {
                return SimpleResponse.success("没有需要清理的旧作业日志");
            }
        } catch (Exception e) {
            log.error("清理旧作业日志失败", e);
            return SimpleResponse.error("清理失败: " + e.getMessage());
        }
    }

    /**
     * 系统健康检查
     */
    @GetMapping("/health")
    public SimpleResponse<String> healthCheck() {
        try {
            Map<String, Object> stats = sparkJobLogsService.getJobStatistics();
            return SimpleResponse.success("Spark作业系统运行正常，统计信息：" + stats.toString());
        } catch (Exception e) {
            log.error("Spark作业系统健康检查失败", e);
            return SimpleResponse.error("系统异常: " + e.getMessage());
        }
    }
}