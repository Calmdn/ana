package com.logistics.service.dao.mapper;

import com.logistics.service.dao.entity.PredictiveAnalysisData;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface PredictiveAnalysisDataMapper {

    /**
     * 插入预测分析数据
     */
    @Insert("INSERT INTO predictive_analysis_data (city, region_id, analysis_date, analysis_hour, " +
            "order_volume, courier_count, avg_duration, total_distance, volume_trend, efficiency_score, " +
            "daily_orders, required_couriers, peak_hour, orders_per_courier_day, capacity_utilization, data_type) " +
            "VALUES (#{city}, #{regionId}, #{analysisDate}, #{analysisHour}, " +
            "#{orderVolume}, #{courierCount}, #{avgDuration}, #{totalDistance}, #{volumeTrend}, #{efficiencyScore}, " +
            "#{dailyOrders}, #{requiredCouriers}, #{peakHour}, #{ordersPerCourierDay}, #{capacityUtilization}, #{dataType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPredictiveAnalysis(PredictiveAnalysisData data);

    /**
     * 根据城市、类型和日期范围查找预测分析
     */
    @Select("SELECT * FROM predictive_analysis_data WHERE city = #{city} AND data_type = #{dataType} " +
            "AND analysis_date >= #{startDate} AND analysis_date <= #{endDate} " +
            "ORDER BY analysis_date DESC")
    List<PredictiveAnalysisData> findByCityAndTypeAndDateRange(@Param("city") String city,
                                                               @Param("dataType") String dataType,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);

    /**
     * 按城市和日期分组获取小时级预测数据
     */
    @Select("SELECT * FROM predictive_analysis_data WHERE city = #{city} AND analysis_date = #{date} " +
            "ORDER BY analysis_hour ASC")
    List<PredictiveAnalysisData> findByCityAndDateGroupByHour(@Param("city") String city,
                                                              @Param("date") LocalDate date);

    /**
     * 获取最新预测数据
     */
    @Select("SELECT * FROM predictive_analysis_data WHERE city = #{city} AND data_type = 'prediction' " +
            "ORDER BY analysis_date DESC " +
            "LIMIT #{limit}")
    List<PredictiveAnalysisData> findLatestPredictions(@Param("city") String city, @Param("limit") int limit);

    /**
     * 获取容量分析数据
     */
    @Select("SELECT * FROM predictive_analysis_data WHERE city = #{city} AND data_type = 'capacity' " +
            "AND analysis_date >= #{startDate} " +
            "ORDER BY analysis_date DESC")
    List<PredictiveAnalysisData> findCapacityAnalysis(@Param("city") String city,
                                                      @Param("startDate") LocalDate startDate);

    /**
     * 获取趋势分析数据
     */
    @Select("SELECT * FROM predictive_analysis_data WHERE city = #{city} AND data_type = 'trend' " +
            "AND analysis_date >= #{startDate} " +
            "ORDER BY analysis_date ASC")
    List<PredictiveAnalysisData> findTrendAnalysis(@Param("city") String city,
                                                   @Param("startDate") LocalDate startDate);

    /**
     * 获取订单量趋势
     */
    @Select("SELECT analysis_date, AVG(order_volume) as avg_order_volume, " +
            "AVG(volume_trend) as avg_volume_trend, " +
            "AVG(required_couriers) as avg_required_couriers " +
            "FROM predictive_analysis_data " +
            "WHERE city = #{city} AND data_type = 'trend' AND analysis_date >= #{startDate} " +
            "GROUP BY analysis_date " +
            "ORDER BY analysis_date ASC")
    List<Map<String, Object>> getOrderVolumeTrend(@Param("city") String city,
                                                  @Param("startDate") LocalDate startDate);

    /**
     * 获取高峰时段分析
     */
    @Select("SELECT peak_hour, COUNT(*) as frequency, " +
            "AVG(order_volume) as avg_order_volume " +
            "FROM predictive_analysis_data " +
            "WHERE city = #{city} AND peak_hour IS NOT NULL " +
            "AND analysis_date >= #{startDate} " +
            "GROUP BY peak_hour " +
            "ORDER BY frequency DESC")
    List<Map<String, Object>> getPeakHourAnalysis(@Param("city") String city,
                                                  @Param("startDate") LocalDate startDate);

    /**
     * 获取容量利用率统计
     */
    @Select("SELECT capacity_utilization, COUNT(*) as count, " +
            "AVG(courier_count) as avg_courier_count, " +
            "AVG(required_couriers) as avg_required_couriers " +
            "FROM predictive_analysis_data " +
            "WHERE city = #{city} AND capacity_utilization IS NOT NULL " +
            "AND analysis_date >= #{startDate} " +
            "GROUP BY capacity_utilization")
    List<Map<String, Object>> getCapacityUtilizationStats(@Param("city") String city,
                                                          @Param("startDate") LocalDate startDate);

    /**
     * 获取效率预测
     */
    @Select("SELECT analysis_date, AVG(efficiency_score) as avg_efficiency_score, " +
            "AVG(orders_per_courier_day) as avg_orders_per_courier " +
            "FROM predictive_analysis_data " +
            "WHERE city = #{city} AND efficiency_score IS NOT NULL " +
            "AND analysis_date >= #{startDate} " +
            "GROUP BY analysis_date " +
            "ORDER BY analysis_date ASC")
    List<Map<String, Object>> getEfficiencyPrediction(@Param("city") String city,
                                                      @Param("startDate") LocalDate startDate);

    /**
     * 根据ID查找预测分析数据
     */
    @Select("SELECT * FROM predictive_analysis_data WHERE id = #{id}")
    PredictiveAnalysisData findById(@Param("id") Long id);

    /**
     * 更新预测分析数据
     */
    @Update("UPDATE predictive_analysis_data SET " +
            "order_volume = #{orderVolume}, courier_count = #{courierCount}, " +
            "efficiency_score = #{efficiencyScore}, required_couriers = #{requiredCouriers} " +
            "WHERE id = #{id}")
    int updatePredictiveAnalysis(PredictiveAnalysisData data);

    /**
     * 删除旧的预测分析数据
     */
    @Delete("DELETE FROM predictive_analysis_data WHERE analysis_date < #{cutoffDate}")
    int cleanupOldPredictions(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 统计预测分析记录数
     */
    @Select("SELECT COUNT(*) FROM predictive_analysis_data WHERE city = #{city} AND data_type = #{dataType}")
    int countByCityAndType(@Param("city") String city, @Param("dataType") String dataType);
}