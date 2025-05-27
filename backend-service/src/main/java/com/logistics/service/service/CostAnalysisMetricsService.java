package com.logistics.service.service;

import com.logistics.service.dao.entity.CostAnalysisMetrics;
import com.logistics.service.dao.mapper.CostAnalysisMetricsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CostAnalysisMetricsService {

    @Autowired
    private CostAnalysisMetricsMapper costAnalysisMetricsMapper;

    /**
     * 保存成本分析数据
     */
    @Transactional
    public int saveCostAnalysis(CostAnalysisMetrics metrics) {
        // 数据验证
        validateCostAnalysisMetrics(metrics);

        // 计算衍生指标
        calculateDerivedMetrics(metrics);

        return costAnalysisMetricsMapper.insertCostAnalysis(metrics);
    }

    /**
     * 批量保存成本分析数据
     */
    @Transactional
    public int batchSaveCostAnalysis(List<CostAnalysisMetrics> metricsList) {
        int count = 0;
        for (CostAnalysisMetrics metrics : metricsList) {
            count += saveCostAnalysis(metrics);
        }
        return count;
    }

    /**
     * 根据城市和日期范围查询成本分析
     */
    public List<CostAnalysisMetrics> getCostAnalysisByCity(String city, LocalDate startDate, LocalDate endDate) {
        return costAnalysisMetricsMapper.findByCityAndDateRange(city, startDate, endDate);
    }

    /**
     * 根据区域和日期范围查询成本分析
     */
    public List<CostAnalysisMetrics> getCostAnalysisByRegion(Integer regionId, LocalDate startDate, LocalDate endDate) {
        return costAnalysisMetricsMapper.findByRegionAndDateRange(regionId, startDate, endDate);
    }

    /**
     * 根据分析类型查询成本分析
     */
    public List<CostAnalysisMetrics> getCostAnalysisByType(String analysisType, LocalDate startDate) {
        return costAnalysisMetricsMapper.findByAnalysisType(analysisType, startDate);
    }

    /**
     * 多条件查询成本分析
     */
    public List<CostAnalysisMetrics> getCostAnalysisByConditions(String city, Integer regionId,
                                                                 String analysisType,
                                                                 LocalDate startDate, LocalDate endDate) {
        return costAnalysisMetricsMapper.findByConditions(city, regionId, analysisType, startDate, endDate);
    }

    /**
     * 获取城市成本趋势
     */
    public List<Map<String, Object>> getCityCostTrend(String city, LocalDate startDate) {
        return costAnalysisMetricsMapper.getCityCostTrend(city, startDate);
    }

    /**
     * 获取区域成本排行
     */
    public List<Map<String, Object>> getRegionCostRanking(String city, LocalDate startDate, int limit) {
        return costAnalysisMetricsMapper.getRegionCostRanking(city, startDate, limit);
    }

    /**
     * 获取分析类型统计
     */
    public List<Map<String, Object>> getAnalysisTypeStats(String city, LocalDate startDate) {
        return costAnalysisMetricsMapper.getAnalysisTypeStats(city, startDate);
    }

    /**
     * 获取高成本告警
     */
    public List<CostAnalysisMetrics> getHighCostAlerts(double threshold, LocalDate date, int limit) {
        return costAnalysisMetricsMapper.findHighCostAlerts(threshold, date, limit);
    }

    /**
     * 获取成本汇总统计
     */
    public Map<String, Object> getCostSummary(String city, LocalDate startDate) {
        return costAnalysisMetricsMapper.getCostSummary(city, startDate);
    }

    /**
     * 更新成本分析数据
     */
    @Transactional
    public int updateCostAnalysis(CostAnalysisMetrics metrics) {
        validateCostAnalysisMetrics(metrics);
        calculateDerivedMetrics(metrics);
        return costAnalysisMetricsMapper.updateCostAnalysis(metrics);
    }

    /**
     * 清理旧数据
     */
    @Transactional
    public int cleanupOldData(LocalDate cutoffDate) {
        return costAnalysisMetricsMapper.cleanupOldCostAnalysis(cutoffDate);
    }

    /**
     * 统计城市成本分析数量
     */
    public int countByCity(String city) {
        return costAnalysisMetricsMapper.countByCity(city);
    }

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