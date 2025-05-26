package com.logistics.service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;

@Data
public class SparkJobLogsDTO {
    private Long id;
    private String jobName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String inputDeliverPath;
    private String inputPickupPath;
    private String outputPath;
    private Long processedRecords;
    private String errorMessage;
    private Integer executionTimeSeconds;
    private String timeFormatUsed;
    private Integer defaultYear;
    private LocalDateTime createdAt;

    // 计算属性
    public String getExecutionDuration() {
        if (executionTimeSeconds != null && executionTimeSeconds > 0) {
            long totalSeconds = executionTimeSeconds;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            if (hours > 0) {
                return String.format("%d小时%d分钟%d秒", hours, minutes, seconds);
            } else if (minutes > 0) {
                return String.format("%d分钟%d秒", minutes, seconds);
            } else {
                return String.format("%d秒", seconds);
            }
        }
        return "未知";
    }

    public BigDecimal getProcessingRate() {
        if (processedRecords != null && processedRecords > 0 && executionTimeSeconds != null && executionTimeSeconds > 0) {
            return BigDecimal.valueOf(processedRecords).divide(BigDecimal.valueOf(executionTimeSeconds), 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public String getJobStatusText() {
        if (status != null) {
            switch (status.toUpperCase()) {
                case "RUNNING": return "运行中";
                case "SUCCESS": return "成功完成";
                case "FAILED": return "执行失败";
                case "PENDING": return "等待执行";
                case "CANCELLED": return "已取消";
                default: return status;
            }
        }
        return "未知状态";
    }

    public String getPerformanceLevel() {
        BigDecimal rate = getProcessingRate();
        if (rate.compareTo(BigDecimal.valueOf(1000)) >= 0) return "高性能";
        if (rate.compareTo(BigDecimal.valueOf(500)) >= 0) return "良好";
        if (rate.compareTo(BigDecimal.valueOf(100)) >= 0) return "一般";
        if (rate.compareTo(BigDecimal.valueOf(10)) >= 0) return "较慢";
        return "低效";
    }

    public boolean isJobCompleted() {
        return "SUCCESS".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status);
    }

    public boolean isJobRunning() {
        return "RUNNING".equalsIgnoreCase(status);
    }

    public String getJobType() {
        if (jobName != null) {
            if (jobName.toLowerCase().contains("delivery")) return "配送分析";
            if (jobName.toLowerCase().contains("pickup")) return "取件分析";
            if (jobName.toLowerCase().contains("comprehensive")) return "综合报告";
            if (jobName.toLowerCase().contains("efficiency")) return "效率分析";
            if (jobName.toLowerCase().contains("spatial")) return "空间分析";
            if (jobName.toLowerCase().contains("cost")) return "成本分析";
            if (jobName.toLowerCase().contains("predictive")) return "预测分析";
        }
        return "通用任务";
    }

    public String getDataVolumeLevel() {
        if (processedRecords != null) {
            if (processedRecords >= 1000000) return "超大数据量";
            if (processedRecords >= 100000) return "大数据量";
            if (processedRecords >= 10000) return "中等数据量";
            if (processedRecords >= 1000) return "小数据量";
            return "极小数据量";
        }
        return "未知";
    }
}