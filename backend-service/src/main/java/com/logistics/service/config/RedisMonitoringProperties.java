package com.logistics.monitoring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
@ConfigurationProperties(prefix = "redis.monitoring")
public class RedisMonitoringProperties {

    /**
     * 是否启用Redis监控
     */
    private boolean enabled = true;

    /**
     * 监控日志存放目录
     */
    private String logDir = "logs/redis";

    /**
     * 需要监控的包列表
     */
    private List<String> includedPackages = Arrays.asList("com.logistics.service");

    /**
     * 排除监控的包列表
     */
    private List<String> excludedPackages = new ArrayList<>();

    /**
     * 需要监控的缓存名称
     */
    private Set<String> includedCaches = new HashSet<>();

    /**
     * 排除监控的缓存名称
     */
    private Set<String> excludedCaches = new HashSet<>();

    /**
     * 最小监控响应时间（毫秒）
     */
    private long minDurationMs = 2;

    /**
     * 是否记录缓存命中的操作
     */
    private boolean logCacheHits = true;

    /**
     * 是否记录缓存未命中的操作
     */
    private boolean logCacheMisses = true;

    /**
     * 日志文件保留天数
     */
    private int maxLogFiles = 30;

    /**
     * 监控详细程度
     */
    private String detailLevel = "STANDARD";
}