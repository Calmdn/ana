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


}