package com.logistics.service.service;

import com.logistics.service.dao.entity.CostAnalysisMetrics;
import com.logistics.service.dao.mapper.CostAnalysisMetricsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CostAnalysisMetricsService {

    @Autowired
    private CostAnalysisMetricsMapper costAnalysisMetricsMapper;

    // ==================== 数据保存操作 ====================

    /**
     * 保存成本分析数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"costs", "stats"}, allEntries = true)
    public int saveCostAnalysis(CostAnalysisMetrics metrics) {
        // 数据验证
        validateCostAnalysisMetrics(metrics);

        // 计算衍生指标
        calculateDerivedMetrics(metrics);

        int result = costAnalysisMetricsMapper.insertCostAnalysis(metrics);
        if (result > 0) {
            log.info("  保存成本分析数据成功，城市: {}，已清除缓存", metrics.getCity());
        }
        return result;
    }

    /**
     * 批量保存成本分析数据 - 保存后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"costs", "stats"}, allEntries = true)
    public int batchSaveCostAnalysis(List<CostAnalysisMetrics> metricsList) {
        int count = 0;
        for (CostAnalysisMetrics metrics : metricsList) {
            // 数据验证
            validateCostAnalysisMetrics(metrics);
            // 计算衍生指标
            calculateDerivedMetrics(metrics);
            count += costAnalysisMetricsMapper.insertCostAnalysis(metrics);
        }

        if (count > 0) {
            log.info("  批量保存成本分析数据成功，共保存 {} 条，已清除缓存", count);
        }
        return count;
    }

    /**
     * 更新成本分析数据 - 更新后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"costs", "stats"}, allEntries = true)
    public int updateCostAnalysis(CostAnalysisMetrics metrics) {
        validateCostAnalysisMetrics(metrics);
        calculateDerivedMetrics(metrics);

        int result = costAnalysisMetricsMapper.updateCostAnalysis(metrics);
        if (result > 0) {
            log.info("  更新成本分析数据成功，已清除缓存");
        }
        return result;
    }

    // ==================== 查询操作 ====================

    /**
     * 根据城市和日期范围查询成本分析 - 添加缓存
     */
    @Cacheable(value = "costs", key = "'city:' + #city + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<CostAnalysisMetrics> getCostAnalysisByCity(String city, LocalDate startDate, LocalDate endDate) {
        log.info("  查询数据库获取城市成本分析[city={}]", city);
        return costAnalysisMetricsMapper.findByCityAndDateRange(city, startDate, endDate);
    }

    /**
     * 根据区域和日期范围查询成本分析 - 添加缓存
     */
    @Cacheable(value = "costs", key = "'region:' + #regionId + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<CostAnalysisMetrics> getCostAnalysisByRegion(Integer regionId, LocalDate startDate, LocalDate endDate) {
        log.info("  查询数据库获取区域成本分析[regionId={}]", regionId);
        return costAnalysisMetricsMapper.findByRegionAndDateRange(regionId, startDate, endDate);
    }

    /**
     * 多条件查询成本分析 - 添加缓存
     */
    @Cacheable(value = "costs",
            key = "'conditions:' + #city + ':' + #regionId + ':' + #analysisType + ':' + #startDate + ':' + #endDate",
            unless = "#result.isEmpty()")
    public List<CostAnalysisMetrics> getCostAnalysisByConditions(String city, Integer regionId,
                                                                 String analysisType,
                                                                 LocalDate startDate, LocalDate endDate) {
        log.info("  查询数据库获取多条件成本分析[city={}, type={}]", city, analysisType);
        return costAnalysisMetricsMapper.findByConditions(city, regionId, analysisType, startDate, endDate);
    }

    /**
     * 获取高成本告警 - 添加缓存
     */
    @Cacheable(value = "costs", key = "'high_cost:' + #threshold + ':' + #date + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<CostAnalysisMetrics> getHighCostAlerts(double threshold, LocalDate date, int limit) {
        log.info("  查询数据库获取高成本告警[threshold={}]", threshold);
        return costAnalysisMetricsMapper.findHighCostAlerts(threshold, date, limit);
    }

    // ==================== 统计分析操作 ====================

    /**
     * 获取城市成本趋势 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'trend:' + #city + ':' + #startDate", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getCityCostTrend(String city, LocalDate startDate) {
        log.info("  查询数据库获取城市成本趋势[city={}]", city);
        return costAnalysisMetricsMapper.getCityCostTrend(city, startDate);
    }

    /**
     * 获取区域成本排行 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'ranking:' + #city + ':' + #startDate + ':' + #limit",
            unless = "#result.isEmpty()")
    public List<Map<String, Object>> getRegionCostRanking(String city, LocalDate startDate, int limit) {
        log.info("  查询数据库获取区域成本排行[city={}]", city);
        return costAnalysisMetricsMapper.getRegionCostRanking(city, startDate, limit);
    }

    /**
     * 获取分析类型统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'type_stats:' + #city + ':' + #startDate", unless = "#result.isEmpty()")
    public List<Map<String, Object>> getAnalysisTypeStats(String city, LocalDate startDate) {
        log.info("  查询数据库获取分析类型统计[city={}]", city);
        return costAnalysisMetricsMapper.getAnalysisTypeStats(city, startDate);
    }

    /**
     * 获取成本汇总统计 - 添加缓存
     */
    @Cacheable(value = "stats", key = "'summary:' + #city + ':' + #startDate", unless = "#result == null")
    public Map<String, Object> getCostSummary(String city, LocalDate startDate) {
        log.info("  查询数据库获取成本汇总统计[city={}]", city);
        return costAnalysisMetricsMapper.getCostSummary(city, startDate);
    }

    /**
     * 统计城市成本分析数量 - 不缓存（简单计数查询）
     */
    public int countByCity(String city) {
        return costAnalysisMetricsMapper.countByCity(city);
    }

    // ==================== 数据维护 ====================

    /**
     * 清理旧数据 - 清理后清除缓存
     */
    @Transactional
    @CacheEvict(value = {"costs", "stats"}, allEntries = true)
    public int cleanupOldData(LocalDate cutoffDate) {
        int result = costAnalysisMetricsMapper.cleanupOldCostAnalysis(cutoffDate);
        if (result > 0) {
            log.info("  清理旧成本分析数据成功，删除 {} 条记录，已清除缓存", result);
        }
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 数据验证
     */
    private void validateCostAnalysisMetrics(CostAnalysisMetrics metrics) {
        if (metrics.getCity() == null || metrics.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
        if (metrics.getDate() == null) {
            throw new IllegalArgumentException("日期不能为空");
        }
        if (metrics.getAnalysisType() == null || metrics.getAnalysisType().trim().isEmpty()) {
            throw new IllegalArgumentException("分析类型不能为空");
        }
        if (metrics.getTotalOrders() == null || metrics.getTotalOrders() < 0) {
            throw new IllegalArgumentException("总订单数不能为负数");
        }
    }

    /**
     * 计算衍生指标
     */
    private void calculateDerivedMetrics(CostAnalysisMetrics metrics) {
        // 计算单均成本
        if (metrics.getTotalCost() != null && metrics.getTotalOrders() != null && metrics.getTotalOrders() > 0) {
            double costPerOrder = metrics.getTotalCost() / metrics.getTotalOrders();
            metrics.setCostPerOrder(costPerOrder);
        }

        // 计算公里成本
        if (metrics.getTotalCost() != null && metrics.getTotalDistance() != null && metrics.getTotalDistance() > 0) {
            double costPerKm = metrics.getTotalCost() / metrics.getTotalDistance();
            metrics.setCostPerKm(costPerKm);
        }

        // 计算燃料成本比例
        if (metrics.getTotalCost() != null && metrics.getTotalFuelCost() != null && metrics.getTotalCost() > 0) {
            double fuelCostRatio = metrics.getTotalFuelCost() / metrics.getTotalCost();
            metrics.setFuelCostRatio(fuelCostRatio);
        }
    }
}