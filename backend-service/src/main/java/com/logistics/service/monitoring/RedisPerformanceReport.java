package com.logistics.service.monitoring;

import com.logistics.service.monitoring.RedisOperationLog;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class RedisPerformanceReport {

    private LocalDate date;
    private Integer totalOperations;
    private Double cacheHitRate;
    private Double avgResponseTime;
    private RedisOperationLog slowestOperation;
    private Map<String, Long> operationsByModule;
    private Map<String, Long> operationsByCache;

    /**
     * 生成报告摘要
     */
    public String generateSummary() {
        return String.format(
                "Redis性能报告 - %s\n" +
                        "总操作数: %d\n" +
                        "缓存命中率: %.2f%%\n" +
                        "平均响应时间: %.2f ms\n" +
                        "最慢操作: %s (%.2f ms)",
                date,
                totalOperations,
                cacheHitRate,
                avgResponseTime,
                slowestOperation != null ? slowestOperation.getMethodName() : "无",
                slowestOperation != null ? slowestOperation.getDurationMs() : 0.0
        );
    }
}