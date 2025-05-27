package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.PredictiveAnalysisData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface PredictiveAnalysisDataMapper {

    /**
     * 插入预测分析数据
     */
    int insertPredictiveAnalysis(PredictiveAnalysisData data);

    /**
     * 批量插入预测分析数据
     */
    int batchInsertPredictiveAnalysis(@Param("list") List<PredictiveAnalysisData> dataList);

    /**
     * 根据城市、类型和日期范围查找预测分析
     */
    List<PredictiveAnalysisData> findByCityAndTypeAndDateRange(@Param("city") String city,
                                                               @Param("dataType") String dataType,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);

    /**
     * 根据城市和日期查找预测数据
     */
    List<PredictiveAnalysisData> findByCityAndDate(@Param("city") String city,
                                                   @Param("date") LocalDate date);

    /**
     * 多条件查询预测数据
     */
    List<PredictiveAnalysisData> findByConditions(@Param("city") String city,
                                                  @Param("regionId") String regionId,
                                                  @Param("dataType") String dataType,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * 获取订单量趋势
     */
    List<Map<String, Object>> getOrderVolumeTrend(@Param("city") String city,
                                                  @Param("dataType") String dataType,
                                                  @Param("startDate") LocalDate startDate);

    /**
     * 获取小时分布分析
     */
    List<Map<String, Object>> getHourlyDistribution(@Param("city") String city,
                                                    @Param("startDate") LocalDate startDate);

    /**
     * 获取效率预测趋势
     */
    List<Map<String, Object>> getEfficiencyTrend(@Param("city") String city,
                                                 @Param("startDate") LocalDate startDate);

    /**
     * 获取容量分析统计
     */
    List<Map<String, Object>> getCapacityAnalysis(@Param("city") String city,
                                                  @Param("startDate") LocalDate startDate);

    /**
     * 获取数据类型统计
     */
    List<Map<String, Object>> getDataTypeStats(@Param("city") String city,
                                               @Param("startDate") LocalDate startDate);

    /**
     * 获取预测汇总统计
     */
    Map<String, Object> getPredictiveSummary(@Param("city") String city,
                                             @Param("startDate") LocalDate startDate);

    /**
     * 获取最新预测数据
     */
    List<PredictiveAnalysisData> findLatestPredictions(@Param("city") String city,
                                                       @Param("dataType") String dataType,
                                                       @Param("limit") int limit);

    /**
     * 更新预测分析数据
     */
    int updatePredictiveAnalysis(PredictiveAnalysisData data);

    /**
     * 删除旧的预测分析数据
     */
    int cleanupOldPredictions(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计预测分析记录数
     */
    int countByCityAndType(@Param("city") String city, @Param("dataType") String dataType);

    /**
     * 获取城市间预测对比
     */
    List<Map<String, Object>> getCityPredictiveComparison(@Param("cities") List<String> cities,
                                                          @Param("dataType") String dataType,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
}