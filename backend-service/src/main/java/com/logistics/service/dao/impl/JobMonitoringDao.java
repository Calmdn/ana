package com.logistics.service.dao.impl;

import com.logistics.service.dao.entity.SparkJobLog;
import com.logistics.service.dao.mapper.SparkJobLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class JobMonitoringDao {

    @Autowired
    private SparkJobLogMapper sparkJobLogMapper;

    public Long startJobTracking(String jobName, String deliverPath, String pickupPath, String outputPath) {
        try {
            SparkJobLog jobLog = new SparkJobLog();
            jobLog.setJobName(jobName);
            jobLog.setStartTime(LocalDateTime.now());
            jobLog.setStatus("RUNNING");
            jobLog.setInputDeliverPath(deliverPath);
            jobLog.setInputPickupPath(pickupPath);
            jobLog.setOutputPath(outputPath);

            sparkJobLogMapper.insertJobLog(jobLog);
            log.info("Started tracking job: {} with ID: {}", jobName, jobLog.getId());
            return jobLog.getId();

        } catch (Exception e) {
            log.error("Failed to start job tracking: {}", e.getMessage());
            return null;
        }
    }

    public void completeJobTracking(Long jobId, boolean success, long processedRecords, String errorMessage) {
        try {
            SparkJobLog jobLog = new SparkJobLog();
            jobLog.setId(jobId);
            jobLog.setEndTime(LocalDateTime.now());
            jobLog.setStatus(success ? "SUCCESS" : "FAILED");
            jobLog.setProcessedRecords(processedRecords);
            jobLog.setErrorMessage(errorMessage);

            sparkJobLogMapper.updateJobLog(jobLog);
            log.info("Completed job tracking for ID: {} with status: {}", jobId, jobLog.getStatus());

        } catch (Exception e) {
            log.error("Failed to complete job tracking: {}", e.getMessage());
        }
    }

    public List<SparkJobLog> getRunningJobs() {
        return sparkJobLogMapper.findRunningJobs();
    }

    public List<SparkJobLog> getRecentJobs(int limit) {
        return sparkJobLogMapper.findRecentJobs(limit);
    }

    public List<Map<String, Object>> getJobStats(int days) {
        return sparkJobLogMapper.getJobStats(days);
    }

    public int cleanupOldLogs() {
        return sparkJobLogMapper.cleanupOldLogs();
    }
}