package com.logistics.service.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedisPerformanceAnalyzer {

    private static final String LOG_DIR = "logs/redis-performance";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析指定日期的Redis性能数据
     */
    public RedisPerformanceReport analyzePerformance(LocalDate date) {
        String logFile = String.format("%s/redis-performance-%s.log",
                LOG_DIR, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        List<RedisOperationLog> logs = readLogsFromFile(logFile);

        return generateReport(logs, date);
    }

    /**
     * 分析最近几天的性能趋势
     */
    public List<RedisPerformanceReport> analyzeTrend(int days) {
        List<RedisPerformanceReport> reports = new ArrayList<>();
        LocalDate endDate = LocalDate.now();

        for (int i = 0; i < days; i++) {
            LocalDate date = endDate.minusDays(i);
            reports.add(analyzePerformance(date));
        }

        return reports;
    }

    private List<RedisOperationLog> readLogsFromFile(String filePath) {
        List<RedisOperationLog> logs = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            log.warn("日志文件不存在: {}", filePath);
            return logs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    RedisOperationLog log = objectMapper.readValue(line, RedisOperationLog.class);
                    logs.add(log);
                } catch (Exception e) {
                    log.error("解析日志行失败: {}", line, e);
                }
            }
        } catch (Exception e) {
            log.error("读取日志文件失败: {}", filePath, e);
        }

        return logs;
    }

    private RedisPerformanceReport generateReport(List<RedisOperationLog> logs, LocalDate date) {
        if (logs.isEmpty()) {
            return RedisPerformanceReport.builder()
                    .date(date)
                    .totalOperations(0)
                    .cacheHitRate(0.0)
                    .avgResponseTime(0.0)
                    .build();
        }

        // 统计总操作数
        int totalOps = logs.size();

        // 统计缓存命中率
        long cacheHits = logs.stream()
                .filter(log -> Boolean.TRUE.equals(log.getCacheHit()))
                .count();
        double hitRate = totalOps > 0 ? (double) cacheHits / totalOps * 100 : 0.0;

        // 统计平均响应时间
        double avgResponseTime = logs.stream()
                .mapToLong(RedisOperationLog::getDurationMs)
                .average()
                .orElse(0.0);

        // 统计最慢的操作
        RedisOperationLog slowestOperation = logs.stream()
                .max(Comparator.comparing(RedisOperationLog::getDurationMs))
                .orElse(null);

        // 按模块统计
        Map<String, Long> operationsByModule = logs.stream()
                .collect(Collectors.groupingBy(
                        RedisOperationLog::getModule,
                        Collectors.counting()
                ));

        // 按缓存名统计
        Map<String, Long> operationsByCache = logs.stream()
                .collect(Collectors.groupingBy(
                        RedisOperationLog::getCacheName,
                        Collectors.counting()
                ));

        return RedisPerformanceReport.builder()
                .date(date)
                .totalOperations(totalOps)
                .cacheHitRate(hitRate)
                .avgResponseTime(avgResponseTime)
                .slowestOperation(slowestOperation)
                .operationsByModule(operationsByModule)
                .operationsByCache(operationsByCache)
                .build();
    }
}