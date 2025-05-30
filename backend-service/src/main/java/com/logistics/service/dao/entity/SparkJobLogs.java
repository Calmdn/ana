package com.logistics.service.dao.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SparkJobLogs {
    private Long id;
    private String jobName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // RUNNING, SUCCESS, FAILED
    private String inputDeliverPath;
    private String inputPickupPath;
    private String outputPath;
    private Long processedRecords;
    private String errorMessage;
    private Integer executionTimeSeconds;
    private String timeFormatUsed;
    private Integer defaultYear;
    private LocalDateTime createdAt;
}