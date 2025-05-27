package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.TimeEfficiencyMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface TimeEfficiencyMetricsMapper {

    /**
     * 插入时间效率数据
     */
    int insertTimeEfficiency(TimeEfficiencyMetrics metrics);

    /**
     * 批量插入时间效率数据
     */
    int batchInsertTimeEfficiency(@Param("list") List<TimeEfficiencyMetrics> metricsList);

    /**
     * 根据城市和日期范围查找时间效率
     */
    List<TimeEfficiencyMetrics> findByCityAndDateRange(@Param("city") String city,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /**
     * 根据城市和日期查找时间效率
     */
    List<TimeEfficiencyMetrics> findByCityAndDate(@Param("city") String city,
                                                  @Param("date") LocalDate date);

    /**
     * 多条件查询时间效率
     */
    List<TimeEfficiencyMetrics> findByConditions(@Param("city") String city,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("minFastRate") Double minFastRate,
                                                 @Param("maxSlowRate") Double maxSlowRate);

    /**
     * 获取配送效率趋势
     */
    List<Map<String, Object>> getDeliveryEfficiencyTrend(@Param("city") String city,
                                                         @Param("startDate") LocalDate startDate);

    /**
     * 获取效率分布统计
     */
    List<Map<String, Object>> getEfficiencyDistribution(@Param("city") String city,
                                                        @Param("startDate") LocalDate startDate);

    /**
     * 获取时间效率排行
     */
    List<Map<String, Object>> getTimeEfficiencyRanking(@Param("cities") List<String> cities,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("limit") int limit);

    /**
     * 获取慢配送分析
     */
    List<TimeEfficiencyMetrics> findSlowDeliveryAnalysis(@Param("city") String city,
                                                         @Param("threshold") double threshold,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("limit") int limit);

    /**
     * 获取快速配送分析
     */
    List<TimeEfficiencyMetrics> findFastDeliveryAnalysis(@Param("city") String city,
                                                         @Param("threshold") double threshold,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("limit") int limit);

    /**
     * 获取时间效率汇总统计
     */
    Map<String, Object> getTimeEfficiencySummary(@Param("city") String city,
                                                 @Param("startDate") LocalDate startDate);

    /**
     * 获取最新时间效率数据
     */
    TimeEfficiencyMetrics findLatestByCity(@Param("city") String city);

    /**
     * 更新时间效率数据
     */
    int updateTimeEfficiency(TimeEfficiencyMetrics metrics);

    /**
     * 删除旧的时间效率数据
     */
    int cleanupOldTimeEfficiency(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计时间效率记录数
     */
    int countByCity(@Param("city") String city);

    /**
     * 获取城市间时间效率对比
     */
    List<Map<String, Object>> getCityTimeEfficiencyComparison(@Param("cities") List<String> cities,
                                                              @Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate);
}