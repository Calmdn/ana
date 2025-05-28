package com.logistics.service.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class RedisPerformanceLogger {

    private static final String LOG_DIR = "logs/redis-performance";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService logExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "redis-performance-logger");
        t.setDaemon(true);
        return t;
    });

    /**
     * 记录Redis操作性能数据
     */
    public void logRedisOperation(RedisOperationLog operationLog) {
        logExecutor.submit(() -> {
            try {
                writeLogToFile(operationLog);
            } catch (Exception e) {
                log.error("写入Redis性能日志失败", e);
            }
        });
    }

    private void writeLogToFile(RedisOperationLog operationLog) throws IOException {
        // 确保日志目录存在
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // 生成日志文件名（按日期）
        String today = LocalDateTime.now().format(DATE_FORMATTER);
        String logFileName = String.format("%s/redis-performance-%s.log", LOG_DIR, today);

        // 将日志对象转换为JSON字符串
        String logJson = objectMapper.writeValueAsString(operationLog);

        // 异步写入文件
        try (FileWriter writer = new FileWriter(logFileName, true)) {
            writer.write(logJson + "\n");
            writer.flush();
        }

        // 同时输出到控制台（可选）
        log.info("Redis性能监控: {}", logJson);
    }
}