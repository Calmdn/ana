package com.logistics.service.service;

import com.logistics.service.dao.entity.RealtimeKpi;
import com.logistics.service.dao.mapper.RealtimeKpiMapper;
import com.logistics.service.dto.KpiDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KpiService {

    @Autowired
    private RealtimeKpiMapper realtimeKpiMapper;

    /**
     * 获取指定城市今天的KPI数据 - 添加缓存
     */
    @Cacheable(value = "kpi", key = "'today:' + #city", unless = "#result.isEmpty()")
    public List<KpiDataDTO> getTodayKpiByCity(String city) {
        try {
            LocalDate today = LocalDate.now();
            log.info("  查询数据库获取今日KPI[city={}, date={}]", city, today);

            List<RealtimeKpi> kpiList = realtimeKpiMapper.findByCityAndDate(city, today);
            return kpiList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取KPI数据失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据城市和日期获取KPI数据 - 添加缓存
     */
    @Cacheable(value = "kpi", key = "'date:' + #city + ':' + #date", unless = "#result.isEmpty()")
    public List<KpiDataDTO> getKpiByDate(String city, LocalDate date) {
        try {
            log.info("  查询数据库获取KPI[city={}, date={}]", city, date);
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
     * 获取最近几天的KPI数据 - 添加缓存
     */
    @Cacheable(value = "kpi", key = "'recent:' + #city + ':' + #days", unless = "#result.isEmpty()")
    public List<KpiDataDTO> getRecentKpi(String city, int days) {
        try {
            log.info("  查询数据库获取最近KPI[city={}, days={}]", city, days);
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
     * 获取系统健康状态 - 添加缓存
     */
    @Cacheable(value = "kpi", key = "'health'")
    public String getSystemHealth() {
        try {
            LocalDate today = LocalDate.now();
            log.info("  查询数据库检查系统健康状态");
            List<RealtimeKpi> todayData = realtimeKpiMapper.findByCityAndDate("Shanghai", today);

            if (todayData.isEmpty()) {
                return "️今日暂无数据";
            }

            return "系统运行正常，共有 " + todayData.size() + " 条记录";

        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage());
            return "系统异常: " + e.getMessage();
        }
    }

    /**
     * 清理旧数据 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = "kpi", allEntries = true)
    public int cleanupOldData(LocalDate cutoffDate) {
        int result = realtimeKpiMapper.cleanupOldKpi(cutoffDate);
        if (result > 0) {
            log.info("清理旧KPI数据成功，删除 {} 条记录，已清除缓存", result);
        }
        return result;
    }

    /**
     * 统计记录数 - 不缓存（简单计数查询）
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