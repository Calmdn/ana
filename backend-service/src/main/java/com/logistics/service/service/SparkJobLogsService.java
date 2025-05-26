package com.logistics.service.service;

import com.logistics.service.dao.entity.SparkJobLogs;
import com.logistics.service.dao.mapper.SparkJobLogsMapper;
import com.logistics.service.dto.SparkJobLogsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
public class SparkJobLogsService {

    @Autowired
    private SparkJobLogsMapper sparkJobLogsMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取最近的作业日志
     */
    public List<SparkJobLogsDTO> getRecentJobLogs(int limit) {
        try {
            String key = "spark_jobs:recent:" + limit;

            @SuppressWarnings("unchecked")
            List<SparkJobLogsDTO> cache = (List<SparkJobLogsDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取最近作业日志, size={}", cache.size());
                return cache;
            }

            log.info("🔍 Redis 未命中，查询 MySQL 最近作业日志");
            List<SparkJobLogs> logs = sparkJobLogsMapper.findRecentJobs(limit);

            List<SparkJobLogsDTO> result = logs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 5, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存最近作业日志，ttl=5m");
            }

            return result;
        } catch (Exception e) {
            log.error("获取最近作业日志失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 按状态获取作业日志
     */
    public List<SparkJobLogsDTO> getJobLogsByStatus(String status) {
        try {
            String key = "spark_jobs:status:" + status;

            @SuppressWarnings("unchecked")
            List<SparkJobLogsDTO> cache = (List<SparkJobLogsDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取状态作业日志[status={}], size={}", status, cache.size());
                return cache;
            }

            List<SparkJobLogs> logs = sparkJobLogsMapper.findByStatus(status);
            List<SparkJobLogsDTO> result = logs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 3, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存状态作业日志[status={}]，ttl=3m", status);
            }

            return result;
        } catch (Exception e) {
            log.error("按状态获取作业日志失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 按作业名称获取日志
     */
    public List<SparkJobLogsDTO> getJobLogsByName(String jobName) {
        try {
            List<SparkJobLogs> logs = sparkJobLogsMapper.findByJobName(jobName);
            return logs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按作业名称获取日志失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取失败的作业
     */
    public List<SparkJobLogsDTO> getFailedJobs() {
        return getJobLogsByStatus("FAILED");
    }

    /**
     * 获取运行中的作业
     */
    public List<SparkJobLogsDTO> getRunningJobs() {
        return getJobLogsByStatus("RUNNING");
    }

    /**
     * 获取成功的作业
     */
    public List<SparkJobLogsDTO> getSuccessfulJobs(int limit) {
        try {
            List<SparkJobLogs> logs = sparkJobLogsMapper.findSuccessfulJobs(limit);
            return logs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取成功作业失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定时间范围的作业日志
     */
    public List<SparkJobLogsDTO> getJobLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            String key = String.format("spark_jobs:range:%s:%s", startTime, endTime);

            @SuppressWarnings("unchecked")
            List<SparkJobLogsDTO> cache = (List<SparkJobLogsDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取时间范围作业日志, size={}", cache.size());
                return cache;
            }

            List<SparkJobLogs> logs = sparkJobLogsMapper.findByTimeRange(startTime, endTime);
            List<SparkJobLogsDTO> result = logs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 15, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存时间范围作业日志，ttl=15m");
            }

            return result;
        } catch (Exception e) {
            log.error("按时间范围获取作业日志失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日作业日志
     */
    public List<SparkJobLogsDTO> getTodayJobs() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return getJobLogsByTimeRange(startOfDay, endOfDay);
    }

    /**
     * 获取长时间运行的作业
     */
    public List<SparkJobLogsDTO> getLongRunningJobs(int thresholdMinutes) {
        try {
            List<SparkJobLogs> logs = sparkJobLogsMapper.findLongRunningJobs(thresholdMinutes * 60);
            return logs.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取长时间运行作业失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取作业统计信息
     */
    public Map<String, Object> getJobStatistics() {
        try {
            String key = "spark_jobs:statistics";

            @SuppressWarnings("unchecked")
            Map<String, Object> cache = (Map<String, Object>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取作业统计信息");
                return cache;
            }

            List<SparkJobLogs> allJobs = sparkJobLogsMapper.findRecentJobs(1000);

            long totalJobs = allJobs.size();
            long successJobs = allJobs.stream().filter(job -> "SUCCESS".equals(job.getStatus())).count();
            long failedJobs = allJobs.stream().filter(job -> "FAILED".equals(job.getStatus())).count();
            long runningJobs = allJobs.stream().filter(job -> "RUNNING".equals(job.getStatus())).count();

            double successRate = totalJobs > 0 ? (double) successJobs / totalJobs * 100 : 0;
            double failureRate = totalJobs > 0 ? (double) failedJobs / totalJobs * 100 : 0;

            // 计算平均执行时间
            double avgExecutionTime = allJobs.stream()
                    .filter(job -> job.getExecutionTimeSeconds() != null)
                    .mapToInt(SparkJobLogs::getExecutionTimeSeconds)
                    .average()
                    .orElse(0.0);

            // 计算总处理记录数
            long totalProcessedRecords = allJobs.stream()
                    .filter(job -> job.getProcessedRecords() != null)
                    .mapToLong(SparkJobLogs::getProcessedRecords)
                    .sum();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalJobs", totalJobs);
            statistics.put("successJobs", successJobs);
            statistics.put("failedJobs", failedJobs);
            statistics.put("runningJobs", runningJobs);
            statistics.put("successRate", Math.round(successRate * 100.0) / 100.0);
            statistics.put("failureRate", Math.round(failureRate * 100.0) / 100.0);
            statistics.put("avgExecutionTime", Math.round(avgExecutionTime));
            statistics.put("totalProcessedRecords", totalProcessedRecords);

            redisTemplate.opsForValue().set(key, statistics, 10, TimeUnit.MINUTES);
            log.info("💾 写入 Redis 缓存作业统计信息，ttl=10m");

            return statistics;
        } catch (Exception e) {
            log.error("获取作业统计失败", e);
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "统计信息获取失败");
            return errorStats;
        }
    }

    /**
     * 获取作业执行趋势
     */
    public List<Map<String, Object>> getJobExecutionTrend(int days) {
        try {
            LocalDateTime startTime = LocalDateTime.now().minusDays(days);
            List<Map<String, Object>> trendData = sparkJobLogsMapper.getJobExecutionTrend(startTime);
            return trendData;
        } catch (Exception e) {
            log.error("获取作业执行趋势失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据ID获取作业详情
     */
    public SparkJobLogsDTO getJobById(Long id) {
        try {
            SparkJobLogs job = sparkJobLogsMapper.findById(id);
            return job != null ? convertToDTO(job) : null;
        } catch (Exception e) {
            log.error("根据ID获取作业失败", e);
            return null;
        }
    }

    /**
     * 清理旧的作业日志
     */
    public int cleanupOldJobs(int daysToKeep) {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
            int deletedCount = sparkJobLogsMapper.cleanupOldJobs(cutoffTime);
            if (deletedCount > 0) {
                // 清除相关缓存
                clearJobsCache();
                log.info("🗑️ 清理旧作业日志完成，删除 {} 条记录", deletedCount);
            }
            return deletedCount;
        } catch (Exception e) {
            log.error("清理旧作业日志失败", e);
            return 0;
        }
    }

    /**
     * 清除作业相关缓存
     */
    private void clearJobsCache() {
        try {
            redisTemplate.delete(redisTemplate.keys("spark_jobs:*"));
            log.info("🗑️ 已清除作业相关缓存");
        } catch (Exception e) {
            log.warn("清除作业缓存失败", e);
        }
    }

    private SparkJobLogsDTO convertToDTO(SparkJobLogs logs) {
        SparkJobLogsDTO dto = new SparkJobLogsDTO();
        dto.setId(logs.getId());
        dto.setJobName(logs.getJobName());
        dto.setStartTime(logs.getStartTime());
        dto.setEndTime(logs.getEndTime());
        dto.setStatus(logs.getStatus());
        dto.setInputDeliverPath(logs.getInputDeliverPath());
        dto.setInputPickupPath(logs.getInputPickupPath());
        dto.setOutputPath(logs.getOutputPath());
        dto.setProcessedRecords(logs.getProcessedRecords());
        dto.setErrorMessage(logs.getErrorMessage());
        dto.setExecutionTimeSeconds(logs.getExecutionTimeSeconds());
        dto.setTimeFormatUsed(logs.getTimeFormatUsed());
        dto.setDefaultYear(logs.getDefaultYear());
        dto.setCreatedAt(logs.getCreatedAt());
        return dto;
    }
}