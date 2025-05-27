package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.SpatialAnalysisMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface SpatialAnalysisMetricsMapper {

    /**
     * 插入空间分析数据
     */
    int insertSpatialAnalysis(SpatialAnalysisMetrics metrics);

    /**
     * 批量插入空间分析数据
     */
    int batchInsertSpatialAnalysis(@Param("list") List<SpatialAnalysisMetrics> metricsList);

    /**
     * 根据城市和日期范围查找空间分析
     */
    List<SpatialAnalysisMetrics> findByCityAndDateRange(@Param("city") String city,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    /**
     * 根据城市和日期查找空间分析
     */
    List<SpatialAnalysisMetrics> findByCityAndDate(@Param("city") String city,
                                                   @Param("date") LocalDate date);

    /**
     * 根据地理范围查找
     */
    List<SpatialAnalysisMetrics> findByGeoRange(@Param("city") String city,
                                                @Param("minLng") Double minLng, @Param("maxLng") Double maxLng,
                                                @Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 获取配送密度热点
     */
    List<Map<String, Object>> getDeliveryDensityHotspots(@Param("city") String city,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("limit") int limit);

    /**
     * 获取配送时间热图数据
     */
    List<Map<String, Object>> getDeliveryTimeHeatmap(@Param("city") String city,
                                                     @Param("startDate") LocalDate startDate);

    /**
     * 获取空间分布统计
     */
    List<Map<String, Object>> getSpatialDistributionStats(@Param("city") String city,
                                                          @Param("startDate") LocalDate startDate);

    /**
     * 获取网格聚合数据
     */
    List<Map<String, Object>> getGridAggregation(@Param("city") String city,
                                                 @Param("date") LocalDate date,
                                                 @Param("gridSize") Double gridSize);

    /**
     * 获取空间汇总统计
     */
    Map<String, Object> getSpatialSummary(@Param("city") String city,
                                          @Param("startDate") LocalDate startDate);

    /**
     * 查找热点区域
     */
    List<SpatialAnalysisMetrics> findHotspotsByCity(@Param("city") String city,
                                                    @Param("date") LocalDate date,
                                                    @Param("limit") int limit);

    /**
     * 查找密度分析数据
     */
    List<SpatialAnalysisMetrics> findDensityAnalysisByCity(@Param("city") String city,
                                                           @Param("date") LocalDate date);

    /**
     * 获取配送员空间分布
     */
    List<Map<String, Object>> getCourierSpatialDistribution(@Param("city") String city,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("limit") int limit);

    /**
     * 更新空间分析数据
     */
    int updateSpatialAnalysis(SpatialAnalysisMetrics metrics);

    /**
     * 删除旧的空间分析数据
     */
    int cleanupOldSpatialData(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计空间分析记录数
     */
    int countByCity(@Param("city") String city);

    /**
     * 获取城市间空间对比
     */
    List<Map<String, Object>> getCitySpatialComparison(@Param("cities") List<String> cities,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
}