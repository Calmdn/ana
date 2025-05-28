package com.logistics.service.monitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RedisOperationLog {

    /**
     * 操作时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    /**
     * 所属模块/服务名
     */
    private String module;

    /**
     * 操作类型（CACHEABLE, CACHE_EVICT, CACHE_PUT等）
     */
    private String operationType;

    /**
     * 缓存名称
     */
    private String cacheName;

    /**
     * 缓存键
     */
    private String cacheKey;

    /**
     * 请求方法名
     */
    private String methodName;

    /**
     * 处理时间（毫秒）
     */
    private Long durationMs;

    /**
     * 操作状态（SUCCESS, FAILURE, CACHE_HIT, CACHE_MISS）
     */
    private String status;

    /**
     * 是否命中缓存
     */
    private Boolean cacheHit;

    /**
     * 数据大小（字节，估算）
     */
    private Long dataSizeBytes;

    /**
     * 错误信息（如果有）
     */
    private String errorMessage;

    /**
     * 额外信息
     */
    private String additionalInfo;

    /**
     * 线程名
     */
    private String threadName;
}