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
     * 获取指定城市今天的KPI数据（先 Redis 缓存，缓存未命中再读 MySQL 并写回缓存）
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
            List<RealtimeKpi> kpiList = realtimeKpiMapper.findBycityAndDate(city, today);

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
     * 获取系统健康状态（简单版）
     */
    public String getSystemHealth() {
        try {
            LocalDate today = LocalDate.now();

            // 检查今天是否有数据
            List<RealtimeKpi> todayData = realtimeKpiMapper.findBycityAndDate("Shanghai", today);

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
     * 实体转DTO
     */
    private KpiDataDTO convertToDTO(RealtimeKpi kpi) {
        KpiDataDTO dto = new KpiDataDTO();
        dto.setCity(kpi.getCity());
        dto.setDate(kpi.getDate());
        dto.setHour(kpi.getHour());
        dto.setTotalOrders(kpi.getTotalOrders());
        dto.setActiveCouriers(kpi.getActiveCouriers());
        dto.setEfficiencyScore(kpi.getEfficiencyScore());

        // 计算快速配送率（这里简化处理）
        if (kpi.getTotalOrders() != null && kpi.getTotalOrders() > 0) {
            // 假设70%的订单是快速配送
            dto.setFastDeliveryRate(kpi.getEfficiencyScore().multiply(java.math.BigDecimal.valueOf(0.7)));
        }

        return dto;
    }

}