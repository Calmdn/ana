package com.logistics.service.service;

import com.logistics.service.dao.entity.RealtimeKpi;
import com.logistics.service.dao.mapper.RealtimeKpiMapper;
import com.logistics.service.dto.KpiDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class KpiService {

    @Autowired
    private RealtimeKpiMapper realtimeKpiMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取指定城市今天的KPI数据
     */
    public List<KpiDataDTO> getTodayKpiByCity(String city) {
        try {
            LocalDate today = LocalDate.now();
            String key = "kpi:" + city + ":" + today;

            // 1. 尝试从 Redis 缓存读取
            @SuppressWarnings("unchecked")
            List<KpiDataDTO> cache = (List<KpiDataDTO>) redisTemplate.opsForValue().get(key);
            if (cache != null) {
                log.info("✅ 从 Redis 缓存获取 KPI[city={}, date={}], size={}", city, today, cache.size());
                return cache;
            }

            // 2. 缓存未命中，查询 MySQL
            log.info("🔍 Redis 未命中，查询 MySQL KPI[city={}, date={}]", city, today);
            List<RealtimeKpi> kpiList = realtimeKpiMapper.findByCityAndDate(city, today);

            // 3. 转换并写回 Redis（30 分钟过期）
            List<KpiDataDTO> result = kpiList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            if (!result.isEmpty()) {
                redisTemplate.opsForValue().set(key, result, 30, TimeUnit.MINUTES);
                log.info("💾 写入 Redis 缓存 KPI[city={}, date={}]，ttl=30m", city, today);
            } else {
                log.warn("⚠️ MySQL 无数据，未写入缓存 city={}, date={}", city, today);
            }

            return result;
        } catch (Exception e) {
            log.error("获取KPI数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据城市和日期获取KPI数据
     */
    public List<KpiDataDTO> getKpiByDate(String city, LocalDate date) {
        try {
            List<RealtimeKpi> kpiList = realtimeKpiMapper.findByCityAndDate(city, date);
            return kpiList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据日期获取KPI数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取最近几天的KPI数据
     */
    public List<KpiDataDTO> getRecentKpi(String city, int days) {
        try {
            List<RealtimeKpi> kpiList = realtimeKpiMapper.findRecentKpiByCity(city, days);
            return kpiList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最近KPI数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取系统健康状态
     */
    public String getSystemHealth() {
        try {
            LocalDate today = LocalDate.now();
            List<RealtimeKpi> todayData = realtimeKpiMapper.findByCityAndDate("Shanghai", today);

            if (todayData.isEmpty()) {
                return "⚠️ 今日暂无数据";
            }

            return "✅ 系统运行正常，共有 " + todayData.size() + " 条记录";

        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage());
            return "❌ 系统异常: " + e.getMessage();
        }
    }

    /**
     * 清理旧数据
     */
    public int cleanupOldData(LocalDate cutoffDate) {
        return realtimeKpiMapper.cleanupOldKpi(cutoffDate);
    }

    /**
     * 统计记录数
     */
    public int countByCity(String city) {
        return realtimeKpiMapper.countByCity(city);
    }

    /**
     * 实体转DTO
     */
    private KpiDataDTO convertToDTO(RealtimeKpi kpi) {
        KpiDataDTO dto = new KpiDataDTO();
        dto.setCity(kpi.getCity());
        dto.setDate(kpi.getDate());
        dto.setHour(kpi.getHour());
        dto.setTotalOrders(kpi.getTotalOrders());
        dto.setActiveCouriers(kpi.getActiveCouriers());
        dto.setCoverageAois(kpi.getCoverageAois());
        dto.setOrdersPerCourier(kpi.getOrdersPerCourier());
        dto.setOrdersPerAoi(kpi.getOrdersPerAoi());
        dto.setEfficiencyScore(kpi.getEfficiencyScore());
        return dto;
    }
}