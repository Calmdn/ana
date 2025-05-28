package com.logistics.service.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class RedisMonitoringAspect {

    @Autowired
    private RedisPerformanceLogger performanceLogger;

    /**
     * 监控所有缓存相关操作
     */
    @Around("@annotation(org.springframework.cache.annotation.Cacheable) || " +
            "@annotation(org.springframework.cache.annotation.CacheEvict) || " +
            "@annotation(org.springframework.cache.annotation.CachePut)")
    public Object monitorCacheOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        LocalDateTime timestamp = LocalDateTime.now();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 获取缓存注解信息
        CacheOperationInfo cacheInfo = extractCacheInfo(method);

        Object result = null;
        String status = "SUCCESS";
        String errorMessage = null;
        boolean cacheHit = false;

        try {
            result = joinPoint.proceed();

            // 判断是否命中缓存（简单判断：如果是查询方法且执行时间很短，可能是缓存命中）
            long duration = System.currentTimeMillis() - startTime;
            if (cacheInfo.operationType.equals("CACHEABLE") && duration < 10) {
                cacheHit = true;
                status = "CACHE_HIT";
            } else if (cacheInfo.operationType.equals("CACHEABLE")) {
                status = "CACHE_MISS";
            }

        } catch (Exception e) {
            status = "FAILURE";
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录Redis操作日志
            RedisOperationLog operationLog = RedisOperationLog.builder()
                    .timestamp(timestamp)
                    .module(className)
                    .operationType(cacheInfo.operationType)
                    .cacheName(cacheInfo.cacheName)
                    .cacheKey(cacheInfo.cacheKey)
                    .methodName(methodName)
                    .durationMs(duration)
                    .status(status)
                    .cacheHit(cacheHit)
                    .dataSizeBytes(estimateDataSize(result))
                    .errorMessage(errorMessage)
                    .additionalInfo(buildAdditionalInfo(joinPoint.getArgs()))
                    .threadName(Thread.currentThread().getName())
                    .build();

            performanceLogger.logRedisOperation(operationLog);
        }

        return result;
    }

    /**
     * 提取缓存操作信息
     */
    private CacheOperationInfo extractCacheInfo(Method method) {
        CacheOperationInfo info = new CacheOperationInfo();

        if (method.isAnnotationPresent(Cacheable.class)) {
            Cacheable cacheable = method.getAnnotation(Cacheable.class);
            info.operationType = "CACHEABLE";
            info.cacheName = cacheable.value().length > 0 ? cacheable.value()[0] : "unknown";
            info.cacheKey = cacheable.key();
        } else if (method.isAnnotationPresent(CacheEvict.class)) {
            CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);
            info.operationType = "CACHE_EVICT";
            info.cacheName = cacheEvict.value().length > 0 ? cacheEvict.value()[0] : "unknown";
            info.cacheKey = cacheEvict.key();
        } else if (method.isAnnotationPresent(CachePut.class)) {
            CachePut cachePut = method.getAnnotation(CachePut.class);
            info.operationType = "CACHE_PUT";
            info.cacheName = cachePut.value().length > 0 ? cachePut.value()[0] : "unknown";
            info.cacheKey = cachePut.key();
        }

        return info;
    }

    /**
     * 估算数据大小
     */
    private Long estimateDataSize(Object data) {
        if (data == null) return 0L;
        try {
            return (long) data.toString().length() * 2; // 简单估算，实际可以用序列化计算
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 构建额外信息
     */
    private String buildAdditionalInfo(Object[] args) {
        if (args == null || args.length == 0) return "";
        return "参数: " + Arrays.toString(args);
    }

    /**
     * 缓存操作信息内部类
     */
    private static class CacheOperationInfo {
        String operationType;
        String cacheName;
        String cacheKey;
    }
}