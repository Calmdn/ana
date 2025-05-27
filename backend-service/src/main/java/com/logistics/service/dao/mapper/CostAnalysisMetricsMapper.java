package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.CostAnalysisMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface CostAnalysisMetricsMapper {

    /**
     * 插入成本分析数据
     */
    int insertCostAnalysis(CostAnalysisMetrics metrics);

    /**
     * 根据城市和日期范围查找成本分析
     */
    List<CostAnalysisMetrics> findByCityAndDateRange(@Param("city") String city,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 根据区域和日期范围查找成本分析
     */
    List<CostAnalysisMetrics> findByRegionAndDateRange(@Param("regionId") Integer regionId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /**
     * 根据分析类型查找成本分析
     */
    List<CostAnalysisMetrics> findByAnalysisType(@Param("analysisType") String analysisType,
                                                 @Param("startDate") LocalDate startDate);

    /**
     * 多条件查询成本分析
     */
    List<CostAnalysisMetrics> findByConditions(@Param("city") String city,
                                               @Param("regionId") Integer regionId,
                                               @Param("analysisType") String analysisType,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 获取城市成本趋势
     */
    List<Map<String, Object>> getCityCostTrend(@Param("city") String city,
                                               @Param("startDate") LocalDate startDate);

    /**
     * 获取区域成本排行
     */
    List<Map<String, Object>> getRegionCostRanking(@Param("city") String city,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("limit") int limit);

    /**
     * 获取分析类型统计
     */
    List<Map<String, Object>> getAnalysisTypeStats(@Param("city") String city,
                                                   @Param("startDate") LocalDate startDate);

    /**
     * 获取高成本告警
     */
    List<CostAnalysisMetrics> findHighCostAlerts(@Param("threshold") double threshold,
                                                 @Param("date") LocalDate date,
                                                 @Param("limit") int limit);

    /**
     * 获取成本汇总统计
     */
    Map<String, Object> getCostSummary(@Param("city") String city,
                                       @Param("startDate") LocalDate startDate);

    /**
     * 更新成本分析数据
     */
    int updateCostAnalysis(CostAnalysisMetrics metrics);

    /**
     * 删除旧的成本分析数据
     */
    int cleanupOldCostAnalysis(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计成本分析数量
     */
    int countByCity(@Param("city") String city);

    /**
     * 批量插入成本分析数据
     */
    int batchInsertCostAnalysis(@Param("list") List<CostAnalysisMetrics> metricsList);

    /**
     * 根据城市获取最新分析数据
     */
    CostAnalysisMetrics findLatestByCity(@Param("city") String city);

    /**
     * 获取成本异常数据
     */
    List<CostAnalysisMetrics> findAnomalyCostData(@Param("city") String city,
                                                  @Param("costThreshold") Double costThreshold,
                                                  @Param("fuelRatioThreshold") Double fuelRatioThreshold,
                                                  @Param("startDate") LocalDate startDate);

    /**
     * 获取城市间成本对比
     */
    List<Map<String, Object>> getCityCostComparison(@Param("cities") List<String> cities,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);
}