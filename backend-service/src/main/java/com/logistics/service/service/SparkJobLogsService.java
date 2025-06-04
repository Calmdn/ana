package com.logistics.service.service;

import com.logistics.service.dao.entity.SparkJobLogs;
import com.logistics.service.dao.mapper.SparkJobLogsMapper;
import com.logistics.service.dto.SparkJobLogsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SparkJobLogsService {

    @Autowired
    private SparkJobLogsMapper sparkJobLogsMapper;

    // ==================== 数据保存操作 ====================

    /**
     * 保存作业日志 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"jobs", "stats"}, allEntries = true)
    public int saveJob(SparkJobLogs job) {
        validateJob(job);

        int result = sparkJobLogsMapper.insertJob(job);
        if (result > 0) {
            log.info("保存作业日志成功，作业: {}，已清除缓存", job.getJobName());
        }
        return result;
    }

    /**
     * 批量保存作业日志 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"jobs", "stats"}, allEntries = true)
    public int batchSaveJobs(List<SparkJobLogs> jobList) {
        for (SparkJobLogs job : jobList) {
            validateJob(job);
        }

        int result = sparkJobLogsMapper.batchInsertJobs(jobList);
        if (result > 0) {
            log.info("批量保存作业日志成功，共保存 {} 条，已清除缓存", result);
        }
        return result;
    }

    /**
     * 更新作业状态 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"jobs", "stats"}, allEntries = true)
    public int updateJobStatus(Long id, String status, LocalDateTime endTime,
                               Integer executionTimeSeconds, String errorMessage) {
        int result = sparkJobLogsMapper.updateJobStatus(id, status, endTime, executionTimeSeconds, errorMessage);
        if (result > 0) {
            log.info("更新作业状态成功，ID: {}，已清除缓存", id);
        }
        return result;
    }

    // ==================== 查询操作 ====================

    /**
     * 获取最近的作业日志 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'recent:' + #limit", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getRecentJobLogs(int limit) {
        try {
            log.info("查询数据库获取最近作业日志[limit={}]", limit);
            List<SparkJobLogs> logs = sparkJobLogsMapper.findRecentJobs(limit);
            return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最近作业日志失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 按状态获取作业日志 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'status:' + #status", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getJobLogsByStatus(String status) {
        try {
            log.info("查询数据库获取状态作业日志[status={}]", status);
            List<SparkJobLogs> logs = sparkJobLogsMapper.findByStatus(status);
            return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按状态获取作业日志失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 按作业名称获取日志 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'name:' + #jobName", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getJobLogsByName(String jobName) {
        try {
            log.info("查询数据库获取作业日志[jobName={}]", jobName);
            List<SparkJobLogs> logs = sparkJobLogsMapper.findByJobName(jobName);
            return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按作业名称获取日志失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取失败的作业 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'failed'", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getFailedJobs() {
        return getJobLogsByStatus("FAILED");
    }

    /**
     * 获取运行中的作业 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'running'", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getRunningJobs() {
        return getJobLogsByStatus("RUNNING");
    }

    /**
     * 获取成功的作业 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'successful:' + #limit", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getSuccessfulJobs(int limit) {
        try {
            log.info("查询数据库获取成功作业[limit={}]", limit);
            List<SparkJobLogs> logs = sparkJobLogsMapper.findSuccessfulJobs(limit);
            return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取成功作业失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取指定时间范围的作业日志 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'range:' + #startTime + ':' + #endTime", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getJobLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            log.info("查询数据库获取时间范围作业日志[{} - {}]", startTime, endTime);
            List<SparkJobLogs> logs = sparkJobLogsMapper.findByTimeRange(startTime, endTime);
            return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("按时间范围获取作业日志失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取今日作业日志 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'today'", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getTodayJobs() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return getJobLogsByTimeRange(startOfDay, endOfDay);
    }

    /**
     * 获取长时间运行的作业 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'long_running:' + #thresholdMinutes", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getLongRunningJobs(int thresholdMinutes) {
        try {
            log.info("查询数据库获取长时间运行作业[threshold={}分钟]", thresholdMinutes);
            List<SparkJobLogs> logs = sparkJobLogsMapper.findLongRunningJobs(thresholdMinutes * 60);
            return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取长时间运行作业失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取有错误的作业 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'with_errors:' + #limit", unless = "#result.isEmpty()")
    public List<SparkJobLogsDTO> getJobsWithErrors(int limit) {
        try {
            log.info("查询数据库获取有错误的作业[limit={}]", limit);
            List<SparkJobLogs> logs = sparkJobLogsMapper.findJobsWithErrors(limit);
            return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取有错误的作业失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据ID获取作业详情 - 添加缓存
     */
    @Cacheable(value = "jobs", key = "'id:' + #id", unless = "#result == null")
    public SparkJobLogsDTO getJobById(Long id) {
        try {
            log.info("查询数据库获取作业详情[id={}]", id);
            SparkJobLogs job = sparkJobLogsMapper.findById(id);
            return job != null ? convertToDTO(job) : null;
        } catch (Exception e) {
            log.error("根据ID获取作业失败", e);
            return null;
        }
    }

    // ==================== 统计分析操作 ====================

    /**
     * 获取作业统计信息 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'statistics'")
    public Map<String, Object> getJobStatistics() {
        try {
            log.info("查询数据库获取作业统计信息");
            Map<String, Object> statistics = new HashMap<>();

            // 获取总数量
            int totalJobs = sparkJobLogsMapper.countTotalJobs();
            int successJobs = sparkJobLogsMapper.countJobsByStatus("SUCCESS");
            int failedJobs = sparkJobLogsMapper.countJobsByStatus("FAILED");
            int runningJobs = sparkJobLogsMapper.countJobsByStatus("RUNNING");

            // 计算比率
            double successRate = totalJobs > 0 ? (double) successJobs / totalJobs * 100 : 0;
            double failureRate = totalJobs > 0 ? (double) failedJobs / totalJobs * 100 : 0;

            // 获取平均执行时间和总处理记录数
            Double avgExecutionTime = sparkJobLogsMapper.getAverageExecutionTime();
            Long totalProcessedRecords = sparkJobLogsMapper.getTotalProcessedRecords();

            statistics.put("totalJobs", totalJobs);
            statistics.put("successJobs", successJobs);
            statistics.put("failedJobs", failedJobs);
            statistics.put("runningJobs", runningJobs);
            statistics.put("successRate", Math.round(successRate * 100.0) / 100.0);
            statistics.put("failureRate", Math.round(failureRate * 100.0) / 100.0);
            statistics.put("avgExecutionTime", avgExecutionTime != null ? Math.round(avgExecutionTime) : 0);
            statistics.put("totalProcessedRecords", totalProcessedRecords != null ? totalProcessedRecords : 0L);

            return statistics;
        } catch (Exception e) {
            log.error("获取作业统计失败", e);
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "统计信息获取失败");
            return errorStats;
        }
    }

    /**
     * 获取作业执行趋势 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'trend:' + #days", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getJobExecutionTrend(int days) {
        try {
            log.info("查询数据库获取作业执行趋势[days={}]", days);
            LocalDateTime startTime = LocalDateTime.now().minusDays(days);
            return sparkJobLogsMapper.getJobExecutionTrend(startTime);
        } catch (Exception e) {
            log.error("获取作业执行趋势失败", e);
            return new ArrayList<>();
        }
    }

    // ==================== 数据维护 ====================

    /**
     * 清理旧的作业日志 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"jobs", "stats"}, allEntries = true)
    public int cleanupOldJobs(int daysToKeep) {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
            int deletedCount = sparkJobLogsMapper.cleanupOldJobs(cutoffTime);
            if (deletedCount > 0) {
                log.info("清理旧作业日志完成，删除 {} 条记录，已清除缓存", deletedCount);
            }
            return deletedCount;
        } catch (Exception e) {
            log.error("清理旧作业日志失败", e);
            return 0;
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 数据验证
     */
    private void validateJob(SparkJobLogs job) {
        if (job.getJobName() == null || job.getJobName().trim().isEmpty()) {
            throw new IllegalArgumentException("作业名称不能为空");
        }
        if (job.getStatus() == null || job.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("作业状态不能为空");
        }
    }

    /**
     * 实体转DTO
     */
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