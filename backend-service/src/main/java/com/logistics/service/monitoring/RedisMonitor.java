package com.logistics.service.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Aspect
@Component
public class RedisMonitor {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 监控所有缓存操作
    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object monitorCache(ProceedingJoinPoint joinPoint) throws Throwable {
        return doMonitor(joinPoint, "CACHE_GET");
    }

    @Around("@annotation(org.springframework.cache.annotation.CachePut)")
    public Object monitorCachePut(ProceedingJoinPoint joinPoint) throws Throwable {
        return doMonitor(joinPoint, "CACHE_PUT");
    }

    @Around("@annotation(org.springframework.cache.annotation.CacheEvict)")
    public Object monitorCacheEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        return doMonitor(joinPoint, "CACHE_DEL");
    }

    private Object doMonitor(ProceedingJoinPoint joinPoint, String operation) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        long startTime = System.currentTimeMillis();
        Object result = null;
        String status = "MISS";

        try {
            result = joinPoint.proceed();

            // 简单判断：有返回值就是命中
            if (result != null && "CACHE_GET".equals(operation)) {
                status = "HIT";
            }

            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 直接打印到控制台
            log.info("REDIS-MONITOR | {} | {} | {}.{}() | {} | {}ms",
                    timestamp, operation, className, methodName, status, duration);
        }
    }
}